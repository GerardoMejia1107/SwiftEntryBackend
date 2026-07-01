# Diagrama Entidad-Relación — SwiftEntry

## Cómo leer la base de datos

La base de datos de SwiftEntry gira alrededor de una idea simple: **una persona escoge asientos en un evento, los aparta, paga y recibe sus boletos**. Todo lo demás existe para hacer eso posible de forma segura y ordenada.

---

### Los usuarios y sus roles

Todo empieza con `users`. Cada persona que usa el sistema —ya sea un comprador, un organizador o un administrador— vive en esa tabla. Su rol viene de `role`, que es lo que determina qué puede hacer dentro de la plataforma. Cada usuario puede tener una dirección (`address`) guardada de forma exclusiva para él, y el sistema también guarda sus tokens de sesión en `refresh_tokens` para mantenerlo conectado de forma segura sin pedirle la contraseña cada rato.

---

### El lado de "qué está a la venta"

Cuando un organizador crea un evento, ese evento queda en la tabla `event` con su fecha, lugar y categoría. Dentro de cada evento pueden existir varias **localidades** (`localities`): zonas con nombre y precio propio, como "VIP", "General" o "Palco". Cada localidad lleva la cuenta de cuántos lugares quedan disponibles.

Los asientos físicos del lugar —cada butaca con su fila y número— viven en `seats`. La tabla `locality_seats` es la que une ambos mundos: dice exactamente qué asiento físico pertenece a qué localidad, y lo más importante, **guarda el estado de ese asiento** (`AVAILABLE`, `RESERVED` o `SOLD`). Si quieres saber si un asiento está ocupado, esa es la tabla que manda.

---

### El lado de "alguien lo está comprando"

Cuando un usuario decide comprar, el sistema crea una `reservation` a su nombre. Esa reserva tiene un tiempo de vida de 15 minutos —si no paga, los asientos se liberan solos. Los asientos específicos que apartó quedan registrados en `reservation_seats`, que también guarda el precio que tenían en ese momento (por si los precios cambian después).

Una vez que paga, se crea un registro en `payment` y, por cada asiento comprado, se genera un `ticket` con su código QR único. Ese boleto es lo que la persona presenta en la entrada para ser validado.

---

### Dónde se encuentran ambos lados

La tabla `locality_seats` es el punto de encuentro. Por un lado le pertenece a una localidad (inventario); por el otro, las líneas de `reservation_seats` apuntan a ella (compra). Su columna `status` es la fuente de verdad: cambia de `AVAILABLE` a `RESERVED` cuando alguien aparta, y a `SOLD` cuando paga.

---

### Flujos adicionales

- **Reembolsos:** si un pago necesita devolverse, se crea un registro en `refund` ligado al pago original. Por ahora esta área está en construcción.
- **Transferencia de boletos:** si alguien cede su boleto a otra persona, eso queda registrado en `ticket_transfer` con quién lo envió y quién lo recibió, junto con la fecha. Es un historial de movimientos, no reemplaza al boleto.
- **Dirección compartida:** la tabla `address` la usan tanto los usuarios (su domicilio) como los eventos (el lugar donde se realizan). Son registros independientes; que compartan tabla es solo para no duplicar estructura.

---

Código DBML para pegar en [dbdiagram.io](https://dbdiagram.io) y generar el diagrama visual de la base de datos.

```dbml
// SwiftEntry Backend — Entity Relationship Diagram
// Generated from JPA entity models

Table role {
  id int [pk, increment]
  name varchar(50) [not null, unique]
  description varchar(255)
  created_at timestamp [not null]
  updated_at timestamp
}

Table address {
  id int [pk, increment]
  street_address varchar(255)
  neighborhood varchar(100)
  municipality varchar(100)
  department varchar(100)
  country varchar(100)
  reference_point varchar(255)
  created_at timestamp [not null]
  updated_at timestamp
}

Table users {
  id int [pk, increment]
  name varchar(100) [not null]
  last_name varchar(100) [not null]
  email varchar(150) [not null, unique]
  phone_number varchar(25)
  dui varchar(20) [unique]
  nit varchar(20) [unique]
  birth_date date
  password_hash varchar [not null]
  is_active boolean [not null, default: true]
  email_verified boolean [not null, default: false]
  address_id int [unique]
  role_id int [not null]
  created_at timestamp [not null]
  updated_at timestamp
}

Table refresh_tokens {
  id int [pk, increment]
  token varchar(36) [not null, unique]
  user_id int [not null]
  expires_at timestamp [not null]
  created_at timestamp [not null]
}

Table event {
  id int [pk, increment]
  name varchar(150) [not null]
  description varchar(1000)
  category varchar(50) [not null, note: 'EventCategory enum']
  organizer_id int [not null]
  address_id int
  start_date timestamp [not null]
  end_date timestamp [not null]
  venue_name varchar(150)
  status varchar(30) [not null, note: 'EventStatus enum']
  image_url varchar(500)
  created_at timestamp [not null]
  updated_at timestamp
}

Table localities {
  id bigint [pk, increment]
  event_id int [not null]
  name varchar(100) [not null]
  description text
  price decimal(10,2) [not null]
  capacity int [not null]
  available_slots int [not null]
  early_bird_discount_percentage decimal(5,2)
  early_bird_deadline timestamp
  version bigint [not null, default: 0, note: '@Version optimistic lock']
  created_at timestamp [not null]
  updated_at timestamp [not null]
}

Table seats {
  id bigint [pk, increment]
  seat_number varchar(20) [not null]
  row_label varchar(10) [not null]
  created_at timestamp [not null]
  updated_at timestamp [not null]
}

Table locality_seats {
  id bigint [pk, increment]
  locality_id bigint [not null]
  seat_id bigint [not null]
  status varchar(30) [not null, default: 'AVAILABLE', note: 'SeatStatus: AVAILABLE / RESERVED / SOLD']
  reserved_by_user_id int
  reservation_expires_at timestamp
  qr_hash varchar(64) [unique]
  version bigint [not null, default: 0, note: '@Version optimistic lock']
  is_active boolean [not null, default: true]
  created_at timestamp [not null]
  updated_at timestamp [not null]

  indexes {
    (seat_id, locality_id) [unique]
  }
}

Table reservation {
  id int [pk, increment]
  user_id int [not null]
  status varchar(30) [not null, note: 'ReservationStatus enum']
  total_amount decimal(10,2) [not null]
  subtotal decimal(10,2) [not null]
  tax_amount decimal(10,2) [not null]
  discount_amount decimal(10,2) [not null]
  reserved_at timestamp [not null]
  purchased_at timestamp
  expires_at timestamp [not null]
  created_at timestamp [not null]
  updated_at timestamp
}

Table reservation_seats {
  id int [pk, increment]
  reservation_id int [not null]
  locality_seat_id bigint [not null]
  price_at_reservation decimal(10,2) [not null]
  created_at timestamp [not null]
  updated_at timestamp
}

Table payment {
  id int [pk, increment]
  reservation_id int [not null]
  amount decimal(10,2) [not null]
  payment_method varchar(30) [not null, note: 'PaymentMethod enum']
  status varchar(30) [not null, note: 'PaymentStatus enum']
  transaction_reference varchar(150)
  paid_at timestamp
  created_at timestamp [not null]
  updated_at timestamp
}

Table ticket {
  id int [pk, increment]
  reservation_id int [not null]
  seat_id bigint [not null]
  ticket_code varchar(100) [not null, unique]
  qr_code varchar(500) [not null, unique]
  status varchar(30) [not null, note: 'TicketStatus enum']
  issued_at timestamp [not null]
  used_at timestamp
  current_holder_id int
  validated_by int
  validated_at timestamp
  created_at timestamp [not null]
  updated_at timestamp
}

Table refund {
  id int [pk, increment]
  payment_id int [not null]
  amount decimal(10,2) [not null]
  reason varchar(255)
  status varchar(30) [not null, note: 'RefundStatus enum']
  refunded_at timestamp
  created_at timestamp [not null]
  updated_at timestamp
}

Table ticket_transfer {
  id int [pk, increment]
  ticket_id int [not null]
  from_user_id int [not null]
  to_user_id int [not null]
  transferred_at timestamp [not null]
  created_at timestamp [not null]
}

// ── Relationships ──────────────────────────────────────────────

// User identity & auth
Ref: users.role_id > role.id
Ref: users.address_id - address.id          // 1:1 cascade+orphanRemoval
Ref: refresh_tokens.user_id > users.id

// Event
Ref: event.organizer_id > users.id
Ref: event.address_id > address.id

// Inventory side: Event → Locality → LocalitySeat ← Seat
Ref: localities.event_id > event.id
Ref: locality_seats.locality_id > localities.id
Ref: locality_seats.seat_id > seats.id
Ref: locality_seats.reserved_by_user_id > users.id  // transient hold

// Purchase side: Reservation → ReservationSeat ← LocalitySeat
Ref: reservation.user_id > users.id
Ref: reservation_seats.reservation_id > reservation.id
Ref: reservation_seats.locality_seat_id > locality_seats.id

// Payment & fulfilment
Ref: payment.reservation_id > reservation.id
Ref: ticket.reservation_id > reservation.id
Ref: ticket.seat_id > seats.id
Ref: ticket.current_holder_id > users.id
Ref: ticket.validated_by > users.id

// Refund
Ref: refund.payment_id > payment.id

// Ticket transfer audit log
Ref: ticket_transfer.ticket_id > ticket.id
Ref: ticket_transfer.from_user_id > users.id
Ref: ticket_transfer.to_user_id > users.id
```
