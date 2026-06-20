---
tags: [swiftentry, entity, ticket]
---

# Boleto

> [!summary]
> Un **Boleto** es el comprobante de entrada con código QR creado **tras un [[Pago]] exitoso** — un boleto por asiento. Empieza `ISSUED` y pasa a `USED` cuando el personal lo escanea en la entrada. Los boletos normalmente nacen dentro de la transacción de pago, no se crean directamente.

Paquete: `domain/Ticket/`

---

## 1. Modelo — `TicketModel` (tabla `ticket`)

| Campo | Significado |
|---|---|
| `reservation` → [[Reserva]] | La reserva confirmada a la que pertenece |
| `seat` → [[Asiento]] | El asiento físico al que da acceso |
| `ticketCode` | Código único humano/escaneo, `TKT-<uuid>` |
| `qrCode` | Payload QR único, `QR-<uuid>` |
| `status` | `ISSUED` / `USED` / `CANCELLED` / `REFUNDED` |
| `issuedAt`, `usedAt` | Marcas de tiempo |
| `validatedBy` → [[Usuario]], `validatedAt` | Quién lo escaneó y cuándo (el personal de la entrada) |

---

## 2. Servicio — `TicketServiceImpl`

| Método | Qué hace |
|---|---|
| `getMyTickets(email)` | Los boletos propios del comprador. Para cada uno, enriquece la respuesta con el **nombre del evento** y el **nombre de la localidad** (buscando el `ReservationSeat` que coincide). Esta es la lectura principal de cara al usuario. |
| `validateTicketByQrCode(qr, validatorId)` | **Escaneo en la entrada.** Solo un boleto `ISSUED` puede validarse (si no, `409`); lo pasa a `USED`, estampa `usedAt`, y registra `validatedBy`/`validatedAt`. |
| `createTicket(req)` | Crear manualmente un boleto para una reserva+asiento (camino admin/utilitario). |
| `getAllTickets`, `getTicketById`, `getTicketByTicketCode`, `getTicketByQrCode`, `getTicketsByReservationId`, `getTicketsByStatus`, `generateTicketsByReservationId` | Varias búsquedas. |
| `updateTicket(id, dto)` | Actualización genérica, incluyendo asignar un validador. |

> [!note] De dónde vienen realmente los boletos
> En operación normal, los boletos los crea en lote `PaymentExecutor` durante el [[Pago]] — `createTicket` es una puerta lateral, no el camino principal.

---

## 3. Controlador

`TicketController` → `/swift_entry/tickets`

| Método y ruta | Propósito | Acceso |
|---|---|---|
| `GET /tickets/me` | Mis boletos (con nombres de evento y localidad) | Autenticado |

> [!warning] La mayoría de los métodos del servicio aún no están expuestos
> La interfaz `TicketService` es rica (validación, búsquedas por código/QR, filtros por estado), pero el **controlador actualmente solo expone `GET /tickets/me`**. En particular, **la validación de QR no tiene endpoint HTTP todavía** — la lógica de escaneo en la entrada existe en el servicio pero no está conectada a una ruta.

---

## 4. Repositorio

`TicketRepository` — búsquedas por reserva, asiento, estado, `ticketCode`, `qrCode`, más `findByReservation_User_Email` (da poder a `getMyTickets`) y guardas `existsBy…`.

---

## 5. DTOs y Mapper
- `TicketRequestDTO` — reservationId + seatId.
- `TicketUpdateDTO` — actualizaciones de estado / validador.
- `TicketResponseDTO` — boleto completo, opcionalmente enriquecido con nombres de evento y localidad.
- `TicketMapper` — `toModel`, `updateModel`, y variantes de `toResponse`.

---

## 6. Relaciones
- Un boleto pertenece a una [[Reserva]] y da acceso a un [[Asiento]].
- Lo crea el [[Pago]] al aprobarse.
- La validación registra al [[Usuario]] del personal de la entrada.

---

## 7. Notas y Detalles a Tener en Cuenta
- 🟡 **Sin endpoint de validación** — `validateTicketByQrCode` está implementado pero sin ruta.
- 🔵 Los códigos QR son cadenas UUID de marcador de posición (`QR-…`), todavía no imágenes QR escaneables reales.
- 🟢 `getMyTickets` es `@Transactional(readOnly = true)` y hace el enriquecimiento de evento/localidad por cada boleto.

## Ver También
- [[Pago]] — lo que emite los boletos
- [[Reserva]] · [[Asiento]]
- [[Flujo de Reserva y Compra]]
