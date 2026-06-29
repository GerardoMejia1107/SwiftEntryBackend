---
tags: [swiftentry, notifications, testing]
---

# Pruebas de Notificaciones

> [!summary]
> Cómo probar las notificaciones in-app que recibe un usuario cuando se libera un asiento en una localidad donde está en lista de espera. Conecta [[Reserva]], [[Localidad]] y la cola de [[Flujo Reserva-Pago-Ticket y Validacion|lista de espera]]. Las notificaciones se guardan en base de datos y el cliente las consulta por REST (polling).

---

## 1. Cómo funciona

Cuando un asiento de una localidad agotada vuelve a quedar libre, la lista de espera marca al siguiente usuario en cola como `NOTIFIED`. En ese momento se crea automáticamente una notificación para ese usuario.

Esto ocurre en tres situaciones, todas dentro de la misma transacción que libera el asiento:

1. Se aprueba un reembolso completo (los asientos de la reserva vuelven a `AVAILABLE`).
2. Se quita un asiento de una reserva existente.
3. Se cancela o expira una reserva.

También se vuelve a generar una notificación cuando el conserje en segundo plano expira una notificación vencida (ventana de 30 minutos) y le pasa el turno al siguiente en la cola.

La notificación queda con `read = false` hasta que el usuario la marca como leída. El usuario nunca crea notificaciones; solo las consulta y las marca leídas.

---

## 2. Prerrequisitos

- Backend levantado (por defecto en `http://localhost:8080`).
- La tabla `notifications` debe existir. Si la base no genera el esquema automáticamente, crearla a mano antes de probar.
- Todos los endpoints requieren el header `Authorization: Bearer <token>` de un usuario autenticado.
- Para forzar una notificación necesitás una localidad **agotada** (`availableSlots = 0`) con al menos un usuario en lista de espera en estado `WAITING`.

---

## 3. Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/swift_entry/notifications/me` | Todas mis notificaciones, más recientes primero |
| GET | `/swift_entry/notifications/me/unread` | Solo las no leídas |
| GET | `/swift_entry/notifications/me/unread-count` | Cantidad de no leídas (`{ "unreadCount": n }`) |
| PATCH | `/swift_entry/notifications/{id}/read` | Marca una como leída |
| PATCH | `/swift_entry/notifications/me/read-all` | Marca todas como leídas |

Todos son de uso del propio usuario; cada quien solo ve y modifica sus notificaciones. Marcar como leída una notificación ajena devuelve `403`.

---

## 4. Pasos para probar

1. Dejar una localidad agotada (`availableSlots = 0`).
2. Con el **Usuario A**, unirse a la lista de espera de esa localidad: `POST /swift_entry/waiting-list` con `{ "localityId": <id> }`. La entrada queda en `WAITING`.
3. Confirmar que A todavía no tiene notificaciones: `GET /swift_entry/notifications/me` devuelve lista vacía y `unread-count` da `0`.
4. Liberar un asiento de esa localidad por cualquiera de las vías: aprobar un reembolso completo de una reserva de la localidad, quitar un asiento de una reserva, o cancelar/expirar una reserva.
5. Volver a consultar con el **Usuario A**: `GET /swift_entry/notifications/me`. Debe aparecer una notificación de tipo `WAITING_LIST_SEAT_AVAILABLE` con el nombre de la localidad y el aviso de los 30 minutos para reservar.
6. `GET /swift_entry/notifications/me/unread-count` debe devolver `{ "unreadCount": 1 }`.
7. Marcar la notificación como leída: `PATCH /swift_entry/notifications/{id}/read`. La respuesta trae `read = true` y `readAt` con la fecha.
8. Volver a pedir `unread-count`: debe dar `0`.

### Casos adicionales

- Crear varias notificaciones y usar `PATCH /swift_entry/notifications/me/read-all`; luego `unread` debe devolver lista vacía.
- Intentar marcar como leída una notificación de otro usuario debe devolver `403`.
- Si la notificación expira sin que A reserve (pasados 30 minutos), el siguiente en la cola recibe su propia notificación cuando el conserje en segundo plano corre.

---

## 5. Notas

- Las notificaciones son solo de lectura para el usuario; el sistema es el único que las genera.
- El canal actual es in-app por consulta REST. Un canal en tiempo real (email o websocket) quedaría como mejora futura encima de este mismo modelo.
- Al liberarse un asiento queda `AVAILABLE` para todos, no reservado en exclusiva para el usuario notificado. La notificación avisa que hay cupo, pero no garantiza el asiento.
