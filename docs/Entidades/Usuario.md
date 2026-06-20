---
tags:
  - swiftentry
  - entity
  - user
---

# Usuario

> [!summary]
> Un **Usuario** es cualquier persona en el sistema — un comprador, un **organizador** de eventos, o un **administrador**. Su nivel de permiso viene de su [[Rol]]. Los usuarios se registran con correo + contraseña (guardada como hash BCrypt), opcionalmente llevan una [[Direccion]], y poseen los tokens de refresco que los mantienen con sesión (ver [[Seguridad y Autenticacion]]).

Paquete: `domain/User/`

---

## 1. Modelo — `UserModel` (tabla `users`)

| Campo | Significado |
|---|---|
| `name`, `lastName`, `email` (único), `phoneNumber` | Identidad y contacto |
| `dui`, `nit` | Documento de identidad / número tributario salvadoreño (únicos) |
| `birthDate` | Fecha de nacimiento |
| `passwordHash` | Hash BCrypt — nunca la contraseña en crudo |
| `isActive`, `emailVerified` | Banderas de la cuenta |
| `addressModel` → [[Direccion]] | Uno-a-uno; cascada y se elimina junto con el usuario |
| `role` → [[Rol]] | El nivel de permiso (requerido) |
| `refreshTokens` | Las sesiones activas del usuario |

---

## 2. Servicio — `UserServiceImpl`

| Método | Qué hace |
|---|---|
| `createUser(req)` | Registro. Rechaza un correo duplicado (`409`), resuelve el [[Rol]], mapea la [[Direccion]], **hashea con BCrypt** la contraseña, y guarda. |
| `getUserById(id)` | Devuelve un usuario — pero solo si **quien llama es ese usuario, o un ADMINISTRATOR**; si no, `403`. La verificación de propiedad lee al usuario con sesión desde el `SecurityContext`. |
| `getAllUsers()` | Todos los usuarios (solo-admin a nivel de ruta). |

> [!note] Dos tipos de verificación de permiso
> Las reglas a nivel de ruta ([[Seguridad y Autenticacion|SecurityRoutes]]) controlan *qué endpoints* puedes tocar; `getUserById` añade una verificación *a nivel de fila* para que un usuario normal pueda leer su propio registro pero no el de otra persona. Ambos estilos aparecen en toda la app.

---

## 3. Controlador

`UserController` → `/swift_entry/users`

| Método y ruta | Propósito | Acceso |
|---|---|---|
| `POST /users` | Registrarse | **Público** |
| `GET /users` | Todos los usuarios | **Admin** |
| `GET /users/{id}` | Un usuario | Autenticado (uno mismo o admin) |

---

## 4. Repositorio
`UserRepository` — `findByEmail` (usado constantemente para resolver al usuario con sesión desde su token), `existsByEmail`.

## 5. DTOs y Mapper
- `UserRequestDTO` — campos de registro incl. una dirección anidada y un `roleId`.
- `UserResponseDTO` — vista segura (sin el hash de la contraseña).
- `UserMapper` — arma el modelo a partir de la petición + el rol resuelto + la contraseña hasheada.

## 6. Relaciones
- Un usuario tiene un [[Rol]] y opcionalmente una [[Direccion]].
- Un usuario **organiza** [[Evento|eventos]], **hace** [[Reserva|reservas]], y **valida** [[Boleto|boletos]] en la entrada.
- Un usuario posee muchos tokens de refresco (ver [[Seguridad y Autenticacion]]).

---

## 7. Notas y Detalles a Tener en Cuenta
- 🟢 Las contraseñas se hashean correctamente con BCrypt (fuerza 12); el DTO de respuesta nunca expone el hash.
- 🔵 Todavía no hay endpoint de actualizar/borrar usuario — solo crear y leer.
- 🔵 El rol llamado `"ADMINISTRATOR"` se referencia por cadena en `getUserById`; mantén consistente ese nombre de [[Rol]].

## Ver También
- [[Rol]] — lo que determina los permisos de un usuario
- [[Direccion]] · [[Seguridad y Autenticacion]]
- [[Reserva]] · [[Evento]]
