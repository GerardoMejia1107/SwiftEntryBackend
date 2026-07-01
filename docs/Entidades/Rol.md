---
tags: [swiftentry, entity, role]
---

# Rol

> [!summary]
> Un **Rol** es el "puesto" de un usuario — ej. `ADMINISTRATOR`, `ORGANIZER`, `CUSTOMER`. Es la entidad más simple de la app, pero importante: el rol de un [[Usuario]] queda incrustado en su JWT y decide qué tiene permitido hacer (ver [[Seguridad y Autenticacion]]).

Paquete: `domain/Role/`

---

## 1. Modelo — `RoleModel` (tabla `role`)

| Campo | Significado |
|---|---|
| `name` (único) | El nombre del rol, ej. `ADMINISTRATOR` |
| `description` | Descripción legible |

> [!note] Los roles son datos, no código
> No hay un enum fijo de roles — son filas en la tabla `role`, creadas vía la API. Spring Security convierte un rol llamado `ADMINISTRATOR` en la autoridad `ROLE_ADMINISTRATOR` (el prefijo `ROLE_` se añade automáticamente).

---

## 2. Servicio — `RoleServiceImpl`

| Método | Qué hace |
|---|---|
| `createRole(req)` | Crea un rol; rechaza un nombre duplicado (`409`). |
| `getAllRoles()` | Lista todos los roles. |

---

## 3. Controlador

`RoleController` → `/swift_entry/roles`

| Método y ruta | Propósito | Acceso |
|---|---|---|
| `POST /roles` | Crear un rol | Autenticado |
| `GET /roles` | Listar roles | **Admin** |

---

## 4. Repositorio
`RoleRepository` — `existsByName`, más el estándar `findById` usado en el registro de [[Usuario]].

## 5. DTOs y Mapper
- `RoleRequestDTO` / `RoleResponseDTO` y `RoleMapper` — directos.

## 6. Relaciones
- Un rol se asigna a muchos [[Usuario|usuarios]] (un rol por usuario).

---

## 7. Notas y Detalles a Tener en Cuenta
- 🔵 Los roles deben existir **antes** de poder crear un usuario con ellos — siembra la tabla `role` (ej. `ADMINISTRATOR`, `ORGANIZER`, `CUSTOMER`) primero.
- 🔵 Sin endpoints de actualizar/borrar.

## Ver También
- [[Usuario]] — quién lleva un rol
- [[Seguridad y Autenticacion]] — cómo el rol impulsa el control de acceso
