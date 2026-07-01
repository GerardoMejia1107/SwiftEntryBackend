---
tags: [swiftentry, entity, locality]
---

# Localidad

> [!summary]
> Una **Localidad** es una **sección con precio dentro de un [[Evento]]** — como "VIP", "General" o "Palco". Lleva el precio que hereda cada [[Asiento]] de esa sección, más un conteo en vivo de cuántos asientos siguen libres (`availableSlots`). Ese contador está protegido por un bloqueo optimista y se mantiene honesto gracias al flujo de [[Reserva]].

Paquete: `domain/Locality/`

---

## 1. Modelo — `LocalityModel` (tabla `localities`)

| Campo | Significado |
|---|---|
| `event` → [[Evento]] | El evento al que pertenece esta sección |
| `name`, `description` | ej. "VIP" |
| `price` | Precio por asiento en esta sección (`BigDecimal`) |
| `capacity` | Total de asientos asignados a ella |
| `availableSlots` | Cuántos siguen libres ahora mismo |
| `version` (`@Version`) | Bloqueo optimista — ver [[Concurrencia y Bloqueo]] |

> [!note] Capacity vs. availableSlots
> `capacity` crece cuando se [[Asiento|asignan asientos]]; `availableSlots` **baja** cuando se reservan asientos y **sube** cuando se liberan. Ambos se mueven en la misma transacción que el cambio de estado del asiento, así que nunca se desincronizan.

---

## 2. Servicio — `LocalityServiceImpl`

| Método | Qué hace |
|---|---|
| `getAllLocalities()` | Todas las localidades. |
| `getLocalityById(id)` | Una localidad. |
| `getLocalitiesByEventId(eventId)` | Secciones de un evento dado. |
| `updateLocality(id, req)` | Actualización parcial vía el mapper. |
| `deleteLocality(id)` | Borrar — **bloqueado si tiene reservas**; de lo contrario cascada a sus asignaciones de asiento. |
| `createLocality(req)` | ⚠️ **Esbozo — devuelve `null`.** Las localidades realmente se crean a través de [[Evento]] (`createEvent` / `updateEvent`), no aquí. |

---

## 3. Controlador

`LocalityController` → `/swift_entry/localities`

| Método y ruta | Propósito | Acceso |
|---|---|---|
| `POST /localities` | Crear (⚠️ no hace nada, ver detalle abajo) | Público |
| `GET /localities` | Todas las localidades | Público |
| `GET /localities/{id}` | Una localidad | Público |
| `GET /localities/event/{eventId}` | Localidades de un evento | Público |
| `PUT /localities/{id}` | Actualizar | Autenticado |
| `DELETE /localities/{id}` | Borrar | Autenticado |

---

## 4. Repositorio
`LocalityRepository` — `findAllByEvent_Id` (usado en todas partes donde se arma el detalle de un evento).

## 5. DTOs y Mapper
- `LocalityRequestDTO`, `LocalityUpdateDTO` (lleva un `id` opcional para la reconciliación al actualizar el evento), `LocalityResponseDTO`.
- `LocalityMapper` — `toModel(dto, event)`, `updateModel(existing, dto)`, `toResponse`.

## 6. Relaciones
- Una localidad pertenece a un [[Evento]].
- Alberga muchos [[Asiento|LocalitySeats]] (sus asignaciones de asiento).
- Su `price` se congela en cada [[Reserva|ReservationSeat]] al momento de reservar.

---

## 7. Notas y Detalles a Tener en Cuenta
- 🔴 **`createLocality` devuelve `null`** y el endpoint `POST /localities` no hace nada útil. Crea las localidades a través de los endpoints de [[Evento]] en su lugar. Este es el punto más claro de "que no te sorprenda" para el equipo.
- 🟢 Los borrados están protegidos contra reservas existentes.

## Ver También
- [[Evento]] — el padre, y donde realmente se crean las localidades
- [[Asiento]] · [[Reserva]] · [[Concurrencia y Bloqueo]]
