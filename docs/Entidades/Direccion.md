---
tags: [swiftentry, entity, address]
---

# Dirección

> [!summary]
> Una **Dirección** es una ubicación postal reutilizable, ligada a un [[Usuario]] (dónde vive) y a un [[Evento]] (dónde se realiza). Es una entidad de datos plana sin lógica de negocio propia.

Paquete: `domain/Address/`

---

## 1. Modelo — `AddressModel` (tabla `address`)

| Campo | Significado |
|---|---|
| `streetAddress`, `neighborhood`, `municipality`, `department`, `country` | Partes estándar de una dirección |
| `referencePoint` | Una pista de referencia (común en El Salvador, ej. "frente a la iglesia") |

---

## 2. Servicio — `AddressServiceImpl`

| Método | Qué hace |
|---|---|
| `createAddress(req)` | Guarda una nueva dirección. |
| `getAddress(id)` | Obtiene una por id (`404` si no existe). |
| `getAllAddresses()` | Lista todas las direcciones. |

En la práctica, las direcciones normalmente se crean **como parte del** registro de [[Usuario]] o de la creación de [[Evento]] (vía el mapper), en lugar de directamente a través de este servicio.

---

## 3. Controlador

`AddressController` → `/swift_entry/addresses`

| Método y ruta | Propósito |
|---|---|
| `POST /addresses` | Crear una dirección |
| `GET /addresses` | Listar direcciones |

---

## 4. Repositorio
`AddressRepository` — `JpaRepository` estándar, sin consultas personalizadas.

## 5. DTOs y Mapper
- `AddressRequestDTO` / `AddressResponseDTO` y `AddressMapper` — copia plana de campos.

## 6. Relaciones
- Uno-a-uno con un [[Usuario]] (`address_id`, en cascada y eliminada junto al usuario).
- Muchos eventos pueden referenciar una dirección ([[Evento]]).

---

## 7. Notas y Detalles a Tener en Cuenta
- 🟡 `GET /addresses` está declarado con un `@RequestBody` (`AddressRequestDTO`) aunque ignora el cuerpo y simplemente lista todas las direcciones — inusual para un GET y conviene limpiarlo.
- 🔵 Sin endpoints de actualizar/borrar.

## Ver También
- [[Usuario]] · [[Evento]]
