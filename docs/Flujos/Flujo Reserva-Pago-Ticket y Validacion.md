# Flujo: Reserva → Pago → Generación de Tickets

## 1. Reserva (`POST /swift_entry/reservations`)

**Clase:** `ReservationServiceImp.createReservation`

El cliente selecciona uno o más asientos (máx. 5) e invoca el endpoint. El flujo dentro de la transacción:

1. **Bloqueo pesimista** — `LocalitySeatRepository.findAllByIdWithLock` adquiere `PESSIMISTIC_WRITE` sobre todos los `LocalitySeat` solicitados de forma atómica para evitar condiciones de carrera.
2. **Validaciones:**
   - Todos los asientos deben existir.
   - Todos deben tener `status = AVAILABLE`.
   - Todos deben pertenecer al mismo evento (evita reservas cruzadas).
3. **Cálculo de montos:**
   - `subtotal` = suma de `locality.price` por asiento.
   - `taxAmount` = subtotal × 0.13 (13 %).
   - `totalAmount` = subtotal + taxAmount.
4. **Escrituras:**
   - Se crea `ReservationModel` con `status = PENDING` y `expiresAt = now + 15 min`.
   - Se crean N registros `ReservationSeatModel` (uno por asiento), que guardan el `priceAtReservation` (snapshot del precio).
   - Cada `LocalitySeat` pasa a `status = RESERVED`, se le asigna `reservedByUser` y `reservationExpiresAt`.
   - `Locality.availableSlots` se decrementa.

**Resultado:** `ReservationResponseDTO` con `status = PENDING`. El cliente tiene 15 minutos para pagar.

> **Scheduler:** `ReservationScheduler` corre cada 60 s y llama a `expirePendingReservations`. Las reservas cuyo `expiresAt` ya pasó se marcan `EXPIRED` y sus asientos vuelven a `AVAILABLE`.

---

## 2. Pago (`POST /swift_entry/payments`)

**Clases:** `PaymentServiceImpl` (no `@Transactional`) + `PaymentExecutor` (`@Transactional`)

La separación en dos beans es intencional: permite que el caso de expiración persista antes de lanzar la excepción, sin que un rollback lo deshaga.

### `PaymentServiceImpl.processPayment` (fuera de transacción)

1. Verifica que la reserva exista y pertenezca al usuario (403 si no).
2. Verifica que la reserva esté en `PENDING` (409 si no).
3. **Chequeo de expiración:** si `now > reservation.expiresAt`, persiste `status = EXPIRED` y lanza `BadRequestException` (400). Este save **sí confirma** porque no hay transacción activa aquí.
4. `simulatePaymentProcessing()` → devuelve `true` (stub; aquí irá la pasarela real).
5. Delega a `PaymentExecutor.execute(reservationId, requestDTO, approved)`.

### `PaymentExecutor.execute` (dentro de transacción)

1. Re-lee la reserva y re-valida `PENDING` + no expirada dentro de la tx (guarda la ventana de concurrencia entre los dos pasos anteriores).
2. Crea `PaymentModel` con `status = PENDING`.

**Si el pago falló (`approved = false`):**
- `payment.status = FAILED`, se guarda, se devuelve la respuesta. La reserva sigue `PENDING` para que el usuario pueda reintentar.

**Si el pago fue aprobado (`approved = true`):**
- `payment.status = APPROVED`, se asigna `paidAt` y `transactionReference = "TXN-<UUID>"`.
- `reservation.status = CONFIRMED`, se asigna `purchasedAt`.
- Por cada `ReservationSeatModel`:
  - `LocalitySeat.status = SOLD`, se limpian `reservationExpiresAt` y `reservedByUser`.
  - Se crea un `TicketModel` con:
    - `ticketCode = "TKT-<UUID>"`
    - `qrCode = "QR-<UUID>"`
    - `status = ISSUED`
    - `issuedAt = now`
- Todo se persiste en una sola transacción: `localitySeat`, `reservation`, `payment`, `tickets`.

**Resultado:** `PaymentResponseDTO` que incluye la lista de `TicketResponseDTO` generados.

---

## 3. Estado final de los tickets

Cada ticket generado parte con:

| Campo          | Valor                         |
|----------------|-------------------------------|
| `status`       | `ISSUED`                      |
| `ticketCode`   | `"TKT-<UUID>"` (único)        |
| `qrCode`       | `"QR-<UUID>"` (único)         |
| `issuedAt`     | timestamp del pago            |
| `validatedBy`  | `null`                        |
| `validatedAt`  | `null`                        |
| `usedAt`       | `null`                        |
| `currentHolder`| `null` → holder implícito es el `reservation.user` |

El único endpoint actualmente expuesto es `GET /tickets/me`, que retorna los tickets del usuario autenticado. El método `validateTicketByQrCode` **existe en el servicio pero no tiene ruta HTTP**.

### Ciclo de vida de un ticket

```
ISSUED ──(validación en puerta)──► USED
       ──(reembolso)─────────────► REFUNDED
       ──(cancelación)───────────► CANCELLED
```

---

## 4. Flujo de transferencia (relevante para validación)

Cuando un ticket se transfiere (`POST /tickets/{id}/transfer`):
- Se rotan `ticketCode` y `qrCode` (los códigos anteriores quedan **invalidados** inmediatamente).
- `currentHolder` apunta al nuevo dueño.
- Se registra `TicketTransferModel` con `fromUser`, `toUser` y `transferredAt`.

Esto significa que en la validación se debe usar el **código actual** del ticket, no uno viejo.

---

## 5. Sugerencias para la validación con Chain of Responsibility

El organizador ingresa el código del ticket (puede ser `ticketCode` o `qrCode`) en un formulario. El backend recibe ese código y lo pasa por una cadena de handlers. Cada handler hace una sola verificación y, si falla, corta la cadena con una excepción descriptiva.

### Handlers sugeridos (en orden)

```
[1] TicketExistsHandler
    → Busca el ticket por código. Lanza 404 si no existe.
    
[2] TicketStatusHandler
    → Verifica que status == ISSUED.
      USED       → 409 "Este ticket ya fue usado."
      CANCELLED  → 409 "Este ticket fue cancelado."
      REFUNDED   → 409 "Este ticket fue reembolsado."

[3] EventOwnershipHandler
    → Verifica que el organizador que valida es el organizador del evento
      al que pertenece el ticket (via ticket → reservation → reservationSeat
      → localitySeat → locality → event → organizer).
      Lanza 403 si no coincide.

[4] EventDateHandler  (opcional pero recomendado)
    → Verifica que la fecha actual esté dentro del rango de fechas del evento.
      Lanza 400 si se intenta validar antes o muy después del evento.
```

### Contexto que necesita cada handler

```java
// Objeto de contexto que viaja por la cadena
public class TicketValidationContext {
    String ticketCode;        // código ingresado por el organizador
    String organizerEmail;    // extraído del JWT
    TicketModel ticket;       // populado por TicketExistsHandler
}
```

### Dónde implementar

- Una interfaz `TicketValidationHandler` con `setNext(handler)` y `handle(context)`.
- Un `TicketValidationChain` (o `TicketValidationService`) que ensambla la cadena y la dispara.
- El `TicketController` recibe el código, construye el contexto con el email del JWT, y llama a la cadena.
- Al final de la cadena exitosa, `TicketServiceImpl.validateTicketByQrCode` (ya implementado) hace el commit: `status = USED`, `usedAt`, `validatedBy`, `validatedAt`.

### Ruta HTTP nueva a agregar

```
POST /swift_entry/tickets/validate
Body: { "ticketCode": "TKT-..." }
Auth: ORGANIZER (o cualquier usuario autenticado con ownership check en el handler)
```
