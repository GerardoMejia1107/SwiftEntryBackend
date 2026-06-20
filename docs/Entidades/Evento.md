---
tags: [swiftentry, entity, event]
---

# Evento

> [!summary]
> Un **Evento** es aquello para lo que la gente compra boletos — un concierto, partido, conferencia, etc. Lo crea un **organizador** ([[Usuario]]), ocurre en una [[Direccion]], y contiene una o más secciones con precio ([[Localidad|localidades]]). Crear un evento también crea sus localidades en una sola llamada.

Paquete: `domain/Event/`

---

## 1. Modelo — `EventModel` (tabla `event`)

| Campo | Significado |
|---|---|
| `name`, `description`, `imageUrl` | Info de visualización |
| `category` | `CONCERT` / `SPORTS` / `THEATER` / `COMEDY` / `CONFERENCE` / `FESTIVAL` / `MOVIE` / `CULTURAL` |
| `organizer` → [[Usuario]] | Quién lo organiza |
| `address` → [[Direccion]] | Dónde se realiza |
| `startDate`, `endDate`, `venueName` | Cuándo/dónde |
| `status` | `DRAFT` / `PUBLISHED` / `CANCELLED` / `FINISHED` |

---

## 2. Servicio — `EventServiceImpl`

| Método | Qué hace |
|---|---|
| `createEvent(req)` | Crea el evento **y sus [[Localidad\|localidades]]** juntos, devolviendo ambos. |
| `getAllEvents()` | Todos los eventos, cada uno con sus localidades adjuntas. |
| `getEventsByOrganizerId(id)` | Eventos que organiza un organizador dado. |
| `getEventById(id)` | Un evento con sus localidades. |
| `updateEvent(id, req)` | Actualización parcial de los campos del evento **y** una reconciliación completa de sus localidades (`processLocalityUpdates`). |
| `deleteEvent(id)` | Borra un evento y sus localidades — **bloqueado si algún asiento tiene reserva** (pide cancelar en su lugar). |

### `processLocalityUpdates` (la parte interesante de actualizar)
Al actualizar, la lista de localidades entrante se reconcilia contra lo que está guardado:
- Las localidades **presentes en la BD pero ausentes en la petición** se borran — a menos que tengan reservas (entonces es un `403`).
- Las localidades **con un id** se actualizan en su lugar.
- Las localidades **sin id** se crean nuevas (deben incluir al menos `name` y `price`).

Esto le permite al organizador agregar/editar/quitar secciones en un solo PUT.

---

## 3. Controlador

`EventController` → `/swift_entry/events`

| Método y ruta | Propósito | Acceso |
|---|---|---|
| `POST /events` | Crear evento + localidades | **Público** (ver detalle abajo) |
| `GET /events` | Todos los eventos | Público |
| `GET /events/organizer/{id}` | Eventos por organizador | Autenticado |
| `GET /events/{id}` | Un evento | Autenticado |
| `PUT /events/{id}` | Actualizar evento + localidades | Autenticado |
| `DELETE /events/{id}` | Borrar evento | Autenticado |

---

## 4. Repositorio
`EventRepository` — `findAllByOrganizer_Id`, más los estándar `existsById`/`findById` usados ampliamente por los servicios de [[Localidad]] y [[Asiento]].

## 5. DTOs, Mappers y Utils
- `EventRequestDTO` (con peticiones de localidad anidadas), `EventUpdateDTO`, `EventResponseDTO`.
- `EventMapper` — `toResponse(event, localities)` agrupa un evento con sus secciones.
- **Utils:** `StringToCategoryConverter` y `StringToStatusConverter` — permiten que cadenas de parámetros de consulta se parseen a los enums `EventCategory` / `EventStatus` automáticamente.

## 6. Relaciones
- Un evento lo organiza un [[Usuario]] y se realiza en una [[Direccion]].
- Contiene muchas [[Localidad|localidades]], que a su vez albergan [[Asiento|asientos]].

---

## 7. Notas y Detalles a Tener en Cuenta
- 🔴 **`createEvent` está tosco / en construcción:** construye un `AddressModel.builder().id(30)` (un id mágico hardcodeado) en lugar de mapear la dirección de la petición, y hardcodea `venueName` a *"Universidad Centroamericana José Simeón Cañas"*. `getOrganizer` usa un `orElseThrow()` pelón (500 genérico si el id del organizador es incorrecto). Este camino necesita limpieza.
- 🟡 `POST /events` es **público** (cualquiera puede crear un evento) — probablemente un marcador de posición, no pensado para producción.
- 🟢 Los borrados y la remoción de localidades están correctamente protegidos contra reservas existentes.

## Ver También
- [[Localidad]] — las secciones dentro de un evento
- [[Asiento]] · [[Usuario]] · [[Direccion]]
- [[Flujo de Reserva y Compra]]
