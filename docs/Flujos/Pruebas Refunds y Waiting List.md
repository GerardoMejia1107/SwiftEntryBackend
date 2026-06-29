# Pruebas — Refunds y Waiting List (rama `feature/refunds`)

> [!warning] Antes de tocar cualquier cosa
> **Analizar bien la lógica de negocio involucrada antes de modificar código**, para no romper los flujos ya existentes (reservas, pagos, tickets, liberación de asientos). Los módulos `Refund` y `WaitingList` están **acoplados** a `Payment`, `Reservation`, `Ticket` y `Locality`: un cambio en cómo se liberan asientos o cómo cambian los estados afecta en cascada a la cola de espera y a los reembolsos. Leer este documento completo y revisar `RefundServiceImpl`, `WaitingListServiceImpl`, `ReservationServiceImp` y `PaymentExecutor` antes de editar.

---

## 0. Prerrequisitos

- Backend levantado (`./mvnw spring-boot:run`), por defecto en `http://localhost:8080`.
- Base de datos sembrada. **Nota:** crear eventos vía `POST /swift_entry/events` falla por un bug de optimistic-lock en `AddressModel`; sembrar evento/localidad/asientos **directamente por SQL**.
- Todos los endpoints (salvo auth) requieren header `Authorization: Bearer <JWT>`.
- `@EnableScheduling` está activo: el `WaitingListScheduler` corre cada 60s y expira notificaciones vencidas.
- Roles relevantes: `ADMINISTRATOR` (gestiona refunds y ve toda la waiting list) y usuario autenticado (consumidor).

---

## 1. Lógica de negocio — Refunds

Reglas aplicadas en `RefundServiceImpl`:

**Crear solicitud (`createRefundRequest`)**
1. El pago debe existir y estar en estado `APPROVED`.
2. El solicitante debe ser el dueño del pago (si no → `403`).
3. No puede haber ya un refund en estado `REQUESTED` o `APPROVED` para ese pago.
4. **Ventana de reembolso:** debe solicitarse al menos **48 h antes** del inicio del evento.
5. El monto no puede exceder el monto original del pago.
6. Se crea en estado `REQUESTED`.

**Aprobar (`approveRefund`, solo ADMIN)**
- Solo refunds en `REQUESTED`.
- Si el monto = monto total del pago → **reembolso completo**: tickets `ISSUED` → `REFUNDED`, se **liberan los asientos** (vuelven a `AVAILABLE`, se reponen `availableSlots`), la reserva → `REFUNDED`, el pago → `REFUNDED`.
- Al liberar asientos se dispara `notifyNextInQueue(...)` → **notifica a la cola de espera** de cada localidad.
- El refund queda en `COMPLETED` con `refundedAt`.

**Rechazar (`rejectRefund`, solo ADMIN)** — solo desde `REQUESTED` → `REJECTED`.

Estados (`RefundStatus`): `REQUESTED → APPROVED → COMPLETED` / `REJECTED` / `FAILED`.

### Endpoints Refunds

| Método | Ruta | Rol | Body |
|--------|------|-----|------|
| `POST` | `/swift_entry/refunds` | Autenticado (dueño) | `RefundRequestDTO` |
| `GET` | `/swift_entry/refunds` | ADMINISTRATOR | — |
| `GET` | `/swift_entry/refunds/{id}` | ADMINISTRATOR | — |
| `GET` | `/swift_entry/refunds/payment/{paymentId}` | Autenticado (dueño) | — |
| `GET` | `/swift_entry/refunds/status/{status}` | ADMINISTRATOR | — |
| `PUT` | `/swift_entry/refunds/{id}` | ADMINISTRATOR | `RefundUpdateDTO` |
| `POST` | `/swift_entry/refunds/{id}/approve` | ADMINISTRATOR | — |
| `POST` | `/swift_entry/refunds/{id}/reject` | ADMINISTRATOR | — |

`RefundRequestDTO`: `{ "paymentId": <int>, "amount": <decimal >= 0.01>, "reason": "<≤255 chars>" }`

### Pasos de prueba — Refunds

1. **Como consumidor**, completar el flujo reserva → pago `APPROVED` de un evento que inicie a **más de 48 h**.
2. `POST /swift_entry/refunds` con el `paymentId` y un `amount` ≤ monto del pago → `201`, estado `REQUESTED`.
3. Reintentar el mismo `POST` → `409` (ya existe refund pendiente).
4. **Como admin**, `GET /swift_entry/refunds` → aparece la solicitud.
5. `POST /swift_entry/refunds/{id}/approve` → `200`, refund `COMPLETED`. Verificar: tickets `REFUNDED`, reserva `REFUNDED`, pago `REFUNDED`, asientos `AVAILABLE` y `availableSlots` repuesto.
6. **Casos de error a validar:** pago no `APPROVED` → `409`; no dueño → `403`; dentro de las 48 h → `400`; `amount` mayor al pago → `400`.

---

## 2. Lógica de negocio — Waiting List (cola cuando está agotado)

Reglas en `WaitingListServiceImpl`:

**Unirse (`joinWaitingList`)**
1. Solo se permite si la localidad está **agotada** (`availableSlots == 0`); si hay cupo → `400` ("reservá directamente").
2. No puede haber ya una entrada activa (`WAITING` o `NOTIFIED`) del mismo usuario en esa localidad → `409`.
3. Entra en estado `WAITING`, ordenada por `createdAt` (FIFO).

**Notificación al liberarse cupo (`notifyNextInQueue`)** — se invoca automáticamente al liberar asientos en:
- Aprobación de refund completo (`RefundServiceImpl.releaseSeats`).
- Quitar un asiento de una reserva (`ReservationServiceImp`, línea ~230).
- Cancelar / expirar una reserva (`ReservationServiceImp`, línea ~320).

Marca a los primeros N de la cola como `NOTIFIED` con una **ventana de 30 min** (`notificationExpiresAt`).

**Confirmación (`fulfillNotifiedEntry`)** — al concretar el pago (`PaymentExecutor`), la entrada `NOTIFIED` del usuario pasa a `FULFILLED`.

**Expiración (`expireNotifiedEntries`, scheduler cada 60s)** — las `NOTIFIED` vencidas pasan a `EXPIRED` y se re-notifica al siguiente en cola si hay cupo.

Estados (`WaitingListStatus`): `WAITING → NOTIFIED → FULFILLED` / `CANCELLED` / `EXPIRED`.

### Endpoints Waiting List

| Método | Ruta | Rol | Body |
|--------|------|-----|------|
| `POST` | `/swift_entry/waiting-list` | Autenticado | `{ "localityId": <long> }` |
| `DELETE` | `/swift_entry/waiting-list/{id}` | Autenticado (dueño) | — |
| `GET` | `/swift_entry/waiting-list/me` | Autenticado | — |
| `GET` | `/swift_entry/waiting-list` | ADMINISTRATOR | — |
| `GET` | `/swift_entry/waiting-list/locality/{localityId}` | ADMINISTRATOR | — |

### Pasos de prueba — Waiting List

1. Dejar una localidad **agotada** (`availableSlots == 0`).
2. **Usuario A** intenta `POST /swift_entry/waiting-list` con cupo disponible → `400`. Con localidad agotada → `201`, estado `WAITING`.
3. Repetir el `POST` con el mismo usuario/localidad → `409`.
4. Liberar un asiento (aprobar un refund, o cancelar/expirar una reserva de esa localidad).
5. `GET /swift_entry/waiting-list/me` (Usuario A) → estado `NOTIFIED` con `notificationExpiresAt` ~30 min.
6. Si A paga dentro de la ventana → entrada `FULFILLED` (verificar tras pago en `PaymentExecutor`).
7. Si A **no** actúa: esperar > 30 min (o adelantar reloj/DB) → el scheduler la marca `EXPIRED` y notifica al siguiente.
8. `DELETE /swift_entry/waiting-list/{id}` → estado `CANCELLED`.

---

## 3. Verificación de los 3 endpoints reportados como faltantes

| Funcionalidad pedida | Estado real | Detalle |
|----------------------|-------------|---------|
| **Waiting list when sold out** | Ya implementado | `POST /swift_entry/waiting-list` solo admite unirse con localidad agotada; set completo de endpoints (unirse/salir/me/all/por localidad). No falta. |
| **Push notification when seat becomes available** | Implementado | La máquina de estados ya estaba cableada en los 3 puntos de liberación de asientos (refund, quitar asiento, cancelar/expirar reserva) + scheduler de expiración. Se agregó el módulo de notificaciones in-app: al pasar una entrada a `NOTIFIED` se crea una notificación para el usuario, consultable por REST (`GET /swift_entry/notifications/me` y relacionados). Ver [[Pruebas de Notificaciones]]. |
| **Occupancy and sales report per event** | No existe | No hay ningún código de reportes/ocupación/ventas en el backend. Falta por completo. |

### Riesgo de negocio a considerar (push de asientos)
Al liberarse, los asientos pasan a `AVAILABLE` para **todos**, no quedan reservados en exclusiva para el usuario `NOTIFIED`. La ventana de 30 min **no protege** el cupo: cualquiera puede tomar el asiento antes que el notificado. Si se quiere un "derecho de preferencia" real, hay que cambiar la liberación para mantener el asiento reservado al usuario notificado durante la ventana. Tener esto en cuenta antes de implementar el push.

---

## Resumen
- **Refunds** y **Waiting list (sold out)**: implementados y funcionales.
- **Notificación de "seat available"**: implementada como notificaciones in-app (ver [[Pruebas de Notificaciones]]). Un canal en tiempo real (email/WebSocket) queda como mejora futura.
- **Reporte de ocupación y ventas por evento**: **falta implementar desde cero**.
