# SwiftEntry Backend

📖 **[Documentación desplegada](https://swift-entry-backend.vercel.app/)**

Backend REST de **SwiftEntry**, una plataforma de venta de boletos para eventos: los organizadores publican eventos con mapas de asientos por localidad, los compradores reservan y pagan sus asientos, y reciben boletos con código QR que se validan en la entrada.

## Stack

- **Java 21** · **Spring Boot 4**
- Spring Web MVC, Spring Data JPA (Hibernate), Spring Security + JWT (JJWT)
- **PostgreSQL**
- Quartz (tareas programadas), Spring Mail, WebSocket, Actuator
- springdoc-openapi (Swagger UI) · ZXing (generación de códigos QR) · Lombok

## Puesta en marcha

Variables de entorno requeridas (ver `.env` local, no versionado):

```
DB_USERNAME=<usuario de postgres>
DB_PASSWORD=<contraseña de postgres>
DB_URI=jdbc:postgresql://<host>:<puerto>/<nombre_db>
```

```bash
# Compilar
./mvnw clean package

# Ejecutar (requiere las variables de entorno de arriba)
./mvnw spring-boot:run

# Correr las pruebas
./mvnw test
```

El esquema de base de datos lo genera Hibernate automáticamente al arrancar (`ddl-auto: update`) — no hay herramienta de migraciones todavía. Todas las rutas de la API viven bajo el prefijo `/swift_entry/`.

## Documentación de la API

Con la app corriendo localmente:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Spec OpenAPI (JSON):** http://localhost:8080/v3/api-docs

También se mantiene una copia exportada del spec y su versión navegable en `docs/`:

- [`docs/openapi.json`](docs/openapi.json) — último spec exportado
- [`docs/Formal Documentation/index.html`](<docs/Formal Documentation/index.html>) — documentación estática (Redoc), la misma que se despliega en el [enlace de arriba](https://swift-entry-backend.vercel.app/). Se regenera con:
  ```bash
  curl -s http://localhost:8080/v3/api-docs -o docs/openapi.json
  npx @redocly/cli build-docs docs/openapi.json --output "docs/Formal Documentation/index.html"
  ```

Una colección de peticiones para [Bruno](https://www.usebruno.com/) organizada por módulo está en [`API Endpoints/`](<API Endpoints>).

## Documentación del sistema (vault de Obsidian)

La carpeta [`docs/`](docs/) es un vault de **Obsidian** con la arquitectura, el modelo de datos y el comportamiento de negocio explicados a fondo, en español. Ábrela en Obsidian para navegar los enlaces internos, o entra directo a estas páginas:

- **[`docs/Inicio.md`](docs/Inicio.md)** — punto de entrada al vault completo.
- **[`docs/Arquitectura/Vision General del Sistema.md`](<docs/Arquitectura/Vision General del Sistema.md>)** — cómo fluye una petición por capas (controlador → servicio → repositorio).
- **[`docs/Arquitectura/Concurrencia y Bloqueo.md`](<docs/Arquitectura/Concurrencia y Bloqueo.md>)** — cómo se evita que dos personas compren el mismo asiento a la vez.
- **[`docs/Arquitectura/Seguridad y Autenticacion.md`](<docs/Arquitectura/Seguridad y Autenticacion.md>)** — login, JWT, tokens de refresco y control de acceso.
- **[`docs/Arquitectura/Infraestructura Compartida.md`](<docs/Arquitectura/Infraestructura Compartida.md>)** — el sobre de respuesta estándar, excepciones y el manejador global de errores.
- **[`docs/Flujos/Flujo de Reserva y Compra.md`](<docs/Flujos/Flujo de Reserva y Compra.md>)** — el recorrido completo: elegir asientos → reservar → pagar → recibir boletos.
- **[`docs/Entidades/`](docs/Entidades/)** — una página por entidad (Modelo · Servicios · Controlador · Repositorio · DTOs/Mappers · Enums).

## Estructura del código

```
src/main/java/com/gerardo/swiftentrybackend/
├── domain/           # una carpeta por entidad (User, Event, Reservation, Payment, Ticket, ...)
│   └── <Entidad>/
│       ├── *Model.java
│       ├── controller/
│       ├── services/
│       ├── repositories/
│       ├── dto/{request,response}/
│       └── utils/ (mapper)
├── security/          # autenticación JWT, tokens de refresco, filtros
├── config/            # seguridad, OpenAPI, reglas de rutas
└── common/            # ResponseBuilder, GeneralResponse, excepciones, manejador global
```

Cada carpeta de dominio sigue el mismo patrón de capas — ver [`docs/Arquitectura/Vision General del Sistema.md`](<docs/Arquitectura/Vision General del Sistema.md>) para el detalle.

## Estado del proyecto

Proyecto universitario en evolución activa. Algunas áreas tienen comportamiento particular que vale la pena conocer antes de integrarse (p. ej. la creación de `Locality` se hace vía `Event`, no tiene endpoint propio). El estado exacto de cada entidad se documenta en su propia página bajo [`docs/Entidades/`](docs/Entidades/).
