---
tags: [swiftentry, architecture, security, auth]
---

# Lab: Seguridad y Autenticación en SwiftEntry

> [!summary]
> En este lab vas a entender **por qué y cómo** funciona toda la capa de seguridad de SwiftEntry — desde que un usuario escribe su contraseña hasta que cada petición protegida se acepta o rechaza. Cada sección explica el concepto, luego conecta ese concepto con el código real del proyecto.

---

## ¿Cuál es el problema que estamos resolviendo?

HTTP es un protocolo **sin estado** (*stateless*). Eso significa que cada petición que llega al servidor es anónima — el servidor no recuerda nada de peticiones anteriores. Si no hacemos nada, cualquier persona puede llamar a cualquier endpoint.

Necesitamos un mecanismo que permita al servidor responder a estas tres preguntas en cada petición:

1. **¿Quién eres?** — Autenticación (*Authentication*)
2. **¿Tienes permiso para hacer esto?** — Autorización (*Authorization*)
3. **Cómo sé que no estás mintiendo?** — Integridad del token

La solución que usa SwiftEntry se llama **autenticación basada en JWT con tokens de refresco**. Es el mismo patrón que usan plataformas como GitHub, Spotify o Netflix.

---

## La estrategia de dos tokens

Antes de ver el código, entiende el diseño general. SwiftEntry emite **dos tokens distintos** al hacer login:

| Token | Vida útil | Dónde vive | Para qué sirve |
|---|---|---|---|
| **Access Token (JWT)** | 15 minutos | Solo en memoria del cliente | Autenticar cada petición HTTP |
| **Refresh Token** | 7 días | Tabla `refresh_tokens` en PostgreSQL | Obtener un nuevo access token sin re-login |

**¿Por qué dos?** Si solo hubiera un token de larga duración, robar ese token comprometería la cuenta por días. Con este esquema, el access token expira en 15 minutos — si alguien lo roba, tiene muy poco tiempo para usarlo. El refresh token dura más pero se puede revocar en la base de datos al instante.

> [!tip] Analogía
> El **access token** es tu pulsera de festival — te identifica rápido en cada entrada pero caduca hoy.
> El **refresh token** es tu boleto original — con él pides una pulsera nueva, pero si lo pierdes puedes reportarlo y te lo cancelan.

---

## Mapa de las piezas

```
config/
  SecurityConfig.java          ← Conecta todo: filtros, reglas, beans
  SecurityRoutes.java          ← Qué URLs son públicas / admin / autenticadas
  CustomUserDetailsService.java ← Puente entre Spring Security y nuestra BD

security/
  jwt/
    JwtService.java            ← Genera y valida tokens JWT
    JwtAuthenticationFilter.java ← Intercepta CADA petición HTTP
  auth/
    controller/AuthController.java   ← POST /login, /refresh, /logout
    services/AuthServiceImpl.java    ← Lógica de login, refresh, logout
    services/RefreshTokenServiceImpl.java ← Ciclo de vida del refresh token
    models/RefreshTokenModel.java    ← Entidad JPA de la tabla refresh_tokens
    dto/...                          ← DTOs de entrada y salida
  handler/
    AuthEntryPoint.java        ← Responde con JSON 401 cuando no hay token
    AppAccessDeniedHandler.java ← Responde con JSON 403 cuando el rol no alcanza
```

---

## Paso 1 — BCrypt: guardar contraseñas de forma segura

### El problema

Si guardas contraseñas en texto plano y la base de datos se filtra, todas las cuentas quedan comprometidas. No se usa MD5 ni SHA-1 — ambos son demasiado rápidos y existe tablas arcoíris para revertirlos.

**BCrypt** fue diseñado específicamente para contraseñas. Sus características clave:
- Incorpora una **sal aleatoria** automáticamente — el mismo password genera hashes distintos en cada llamada.
- Tiene un **factor de costo** (strength) que controla cuánto tarda — más lento es mejor porque dificulta ataques de fuerza bruta.
- **Es unidireccional** — no hay forma de revertir el hash a la contraseña original.

### En el código

El bean `PasswordEncoder` se define en `SecurityConfig`:

```java
// SecurityConfig.java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

El `12` es el factor de costo (*strength*). Cada incremento de 1 **duplica** el tiempo de cómputo. Con factor 12, hashear una contraseña tarda ~250ms — suficientemente lento para atacantes, suficientemente rápido para usuarios.

Cuando un usuario se registra (`UserServiceImpl.createUser`), la contraseña se hashea antes de guardar:

```java
// El hash se genera UNA vez al registrar y se guarda en la BD
user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
```

Cuando hace login, BCrypt **compara** (nunca descifra):

```java
// Spring lo hace internamente — compara el plain text con el hash guardado
passwordEncoder.matches("miContraseña", "$2a$12$hash...");
```

> [!note] ¿Por qué no puedo "recuperar" una contraseña?
> Porque el hash es matemáticamente irreversible. Los sistemas de "recuperar contraseña" siempre **resetean** la contraseña, nunca la muestran — porque nadie la conoce.

---

## Paso 2 — CustomUserDetailsService: el puente con la base de datos

Spring Security necesita una forma de cargar un usuario dado su "username" para verificar credenciales. Por defecto buscaría en memoria. Nosotros le decimos que busque en nuestra tabla `users`.

```java
// config/CustomUserDetailsService.java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole().getName())
                .build();
    }
}
```

**Puntos clave:**
- `username` aquí es el **correo electrónico** — Spring Security usa el término genérico "username" pero nosotros le pasamos el email.
- Devuelve un `UserDetails` — un contrato de Spring Security con `username`, `password` y `authorities` (roles).
- El método `.roles("ADMINISTRATOR")` añade automáticamente el prefijo `ROLE_` → la autoridad queda como `ROLE_ADMINISTRATOR`. Esto es importante para entender `hasRole("ADMINISTRATOR")` más adelante.

---

## Paso 3 — JwtService: generar y validar tokens

### ¿Qué es un JWT?

Un JWT (*JSON Web Token*) tiene tres partes separadas por puntos:

```
eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9DVVNUT01FUiIsInN1YiI6InVzZXJAZ...
│                   │ │                                                    │ │          │
└── Header (base64) ┘ └─────────────── Payload (base64) ──────────────────┘ └─ Firma ─┘
```

- **Header**: algoritmo usado (`HS256`)
- **Payload**: los datos (*claims*) — quién es el usuario, cuándo expira, su rol
- **Firma**: el hash de `header + payload` usando la clave secreta — si alguien modifica el payload, la firma no coincide y el token se rechaza

> [!important] El payload NO está cifrado
> Está codificado en base64 — cualquiera puede decodificarlo y leer su contenido. Por eso nunca guardes datos sensibles (contraseñas, datos bancarios) en un JWT. Lo que hace seguro al JWT es la **firma**, no el secreto del contenido.

### Generar un token

```java
// security/jwt/JwtService.java
public String generateToken(UserDetails userDetails) {
    String role = userDetails.getAuthorities()
            .stream()
            .findFirst()
            .orElseThrow()
            .getAuthority();  // ej. "ROLE_CUSTOMER"

    return generateToken(
            Map.of("role", role),  // extra claim que añadimos nosotros
            userDetails
    );
}

public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
            .claims(extraClaims)                                     // {role: "ROLE_CUSTOMER"}
            .subject(userDetails.getUsername())                      // el email del usuario
            .issuedAt(new Date())                                    // cuándo se emitió
            .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // expira en 15 min
            .signWith(getSigningKey(), Jwts.SIG.HS256)               // firma con HS256
            .compact();                                              // serializa a String
}
```

La clave de firma viene de `application.yaml`:

```yaml
# application.yaml
jwt:
  secret-key: "JinxCyberPunkEffectPropagationsB"  # ⚠️ mover a variable de entorno en producción
  expiration-ms: 900000    # 15 minutos en milisegundos
  refresh-expiration-days: 7
```

```java
private SecretKey getSigningKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
}
```

### Validar un token

```java
public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
}

private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
}

private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())  // verifica la firma — si falló, lanza JwtException
            .build()
            .parseSignedClaims(token)
            .getPayload();

    return claimsResolver.apply(claims);
}
```

`parseSignedClaims` hace dos cosas a la vez: **verifica la firma** y **parsea el payload**. Si alguien modificó el token, la firma no coincide y lanza una excepción — el token se rechaza.

---

## Paso 4 — El Login: cómo se autentican los usuarios

### El flujo completo

```mermaid
sequenceDiagram
    participant C as Cliente
    participant AC as AuthController
    participant AS as AuthServiceImpl
    participant AM as AuthenticationManager
    participant CUDS as CustomUserDetailsService
    participant JS as JwtService
    participant RTS as RefreshTokenService

    C->>AC: POST /auth/login { email, password }
    AC->>AS: login(authRequestDTO)
    AS->>AM: authenticate( UsernamePasswordAuthenticationToken )
    AM->>CUDS: loadUserByUsername(email)
    CUDS-->>AM: UserDetails { email, passwordHash, roles }
    Note over AM: BCrypt.matches(password, passwordHash)
    AM-->>AS: Authentication (OK) o lanza BadCredentialsException
    AS->>JS: generateToken(userDetails) → accessToken
    AS->>RTS: createRefreshToken(email) → refreshToken en BD
    AS-->>C: { accessToken, refreshToken, tokenType:"Bearer", role, user }
```

### El código

```java
// security/auth/controller/AuthController.java
@PostMapping("/login")
public ResponseEntity<GeneralResponse> login(@Valid @RequestBody AuthRequestDTO requestDTO) {
    AuthResponseDTO authResponseDTO = authService.login(requestDTO);
    return responseBuilder.buildResponse("Login successful", HttpStatus.OK, authResponseDTO);
}
```

```java
// security/auth/services/AuthServiceImpl.java
@Override
public AuthResponseDTO login(AuthRequestDTO authRequestDTO) {
    // 1. Spring Security verifica email + contraseña contra la BD
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    authRequestDTO.getEmail(),
                    authRequestDTO.getPassword()
            )
    );

    // 2. Si llegamos aquí, las credenciales son válidas
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    // 3. Generamos el access token (JWT)
    String accessToken = jwtService.generateToken(userDetails);
    String role = userDetails.getAuthorities().stream().findFirst().orElseThrow().getAuthority();

    // 4. Creamos y guardamos el refresh token en la BD
    RefreshTokenModel refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

    // 5. Buscamos datos del usuario para incluirlos en la respuesta
    UserModel userModel = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return AuthResponseDTO.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getToken())
            .tokenType("Bearer")
            .role(role)
            .user(AuthUserDTO.builder()
                    .id(userModel.getId())
                    .name(userModel.getName())
                    .lastName(userModel.getLastName())
                    .email(userModel.getEmail())
                    .role(role)
                    .build())
            .build();
}
```

**¿Qué hace `authenticationManager.authenticate(...)`?**  
Internamente llama a `CustomUserDetailsService.loadUserByUsername(email)` para obtener el usuario de la BD, luego llama a `passwordEncoder.matches(plainText, hash)` para comparar contraseñas. Si no coinciden, lanza `BadCredentialsException` que el `GlobalExceptionHandler` convierte en 401.

### La respuesta que recibe el cliente

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9DVVN...",
  "refreshToken": "a3f8b2c1-4d5e-6789-abcd-ef0123456789",
  "tokenType": "Bearer",
  "role": "ROLE_CUSTOMER",
  "user": {
    "id": 42,
    "name": "Gerardo",
    "lastName": "Mejia",
    "email": "gerardo@email.com",
    "role": "ROLE_CUSTOMER"
  }
}
```

El cliente guarda el `accessToken` y lo envía en cada petición posterior como:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Paso 5 — JwtAuthenticationFilter: el guardián de cada petición

Este es el componente más importante de la cadena. Se ejecuta **antes de que cualquier controlador procese la petición**.

```java
// security/jwt/JwtAuthenticationFilter.java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. ¿Hay header Authorization?
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Sin token → pasamos la petición al siguiente filtro
            // Las rutas públicas funcionarán; las privadas serán rechazadas después
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (quitar el prefijo "Bearer ")
        final String jwt = authHeader.substring(7);
        final String userEmail;

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            sendUnauthorizedResponse(response, request, "Access token has expired");
            return; // Cortar la cadena — no seguimos
        } catch (JwtException e) {
            sendUnauthorizedResponse(response, request, "Invalid or malformed token");
            return;
        }

        // 3. Si tenemos email Y el usuario aún no está autenticado en este contexto
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 4. Crear el objeto de autenticación y registrarlo en el SecurityContext
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Continuar con la cadena (llegar al controlador)
        filterChain.doFilter(request, response);
    }
}
```

### El SecurityContext

`SecurityContextHolder.getContext().setAuthentication(authToken)` es la línea más crítica. Esto le dice a Spring Security: *"el usuario actual de esta petición es X con el rol Y"*. Desde este momento, cualquier controlador puede preguntar quién está autenticado:

```java
// En cualquier controlador o servicio
authentication.getName()  // devuelve el email
authentication.getAuthorities()  // devuelve los roles
```

> [!note] ¿Por qué `OncePerRequestFilter`?
> En arquitecturas complejas de Spring, los filtros pueden ejecutarse múltiples veces por petición (por ejemplo si hay forwards internos). `OncePerRequestFilter` garantiza que la lógica del filtro corre **exactamente una vez** por petición HTTP real.

### Flujo visual del filtro

```mermaid
flowchart TD
    A[Petición HTTP llega] --> B{¿Tiene header\nAuthorization Bearer?}
    B -->|No| C[Pasar al siguiente filtro\nsin autenticar]
    B -->|Sí| D[Extraer el JWT]
    D --> E{¿Token válido?}
    E -->|Expirado| F[Responder 401 JSON\n'Access token has expired']
    E -->|Malformado| G[Responder 401 JSON\n'Invalid or malformed token']
    E -->|OK| H[Cargar usuario de la BD]
    H --> I[Registrar en SecurityContext]
    I --> J[Pasar al siguiente filtro / controlador]
    C --> J
```

---

## Paso 6 — SecurityConfig: la configuración central

```java
// config/SecurityConfig.java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS: solo permite peticiones desde http://localhost:5173 (el frontend)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // CSRF: deshabilitado porque usamos JWTs, no cookies de sesión
            .csrf(AbstractHttpConfigurer::disable)

            // Form login y Basic Auth: deshabilitados — usamos nuestro propio flujo
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // Registrar nuestro proveedor de autenticación (BCrypt + BD)
            .authenticationProvider(authenticationProvider())

            // Insertar el filtro JWT ANTES del filtro de usuario/contraseña por defecto
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // Manejadores de error personalizados (JSON en vez del HTML de Spring)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint)       // 401
                .accessDeniedHandler(appAccessDeniedHandler)    // 403
            )

            // Reglas de acceso por ruta
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, SecurityRoutes.PUBLIC_POST_ENDPOINTS).permitAll()
                .requestMatchers(SecurityRoutes.PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.GET, SecurityRoutes.AUTHENTICATED_GET_ENDPOINTS).authenticated()
                .requestMatchers(HttpMethod.GET, SecurityRoutes.ADMIN_GET_ENDPOINTS).hasRole("ADMINISTRATOR")
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

**¿Por qué deshabilitar CSRF?**  
CSRF (*Cross-Site Request Forgery*) es un ataque donde una página maliciosa hace peticiones en nombre de un usuario que tiene una cookie de sesión activa. Como SwiftEntry usa **JWTs en el header `Authorization`** (no cookies), ese vector de ataque no aplica.

**¿Qué es `DaoAuthenticationProvider`?**  
Es el componente de Spring que conecta `CustomUserDetailsService` (cargar usuario) con `PasswordEncoder` (verificar contraseña). Lo configuramos nosotros para que use nuestros beans en vez de los defaults.

---

## Paso 7 — SecurityRoutes: las reglas de acceso

```java
// config/SecurityRoutes.java
public final class SecurityRoutes {

    // Cualquier persona puede acceder (sin token)
    public static final String[] PUBLIC_ENDPOINTS = {
            "/swift_entry/auth/**",
            "/swift_entry/events",
            "/swift_entry/localities", "/swift_entry/localities/**",
            "/swift_entry/seats", "/swift_entry/seats/**"
    };

    // POST sin token (registro de usuario y creación de evento)
    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/swift_entry/users", "/swift_entry/events"
    };

    // Solo el rol ADMINISTRATOR puede hacer GET a estas rutas
    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/swift_entry/users", "/swift_entry/events", "/swift_entry/roles",
            "/swift_entry/reservations", "/swift_entry/reservations/*"
    };

    // Cualquier usuario con sesión puede hacer GET a estas rutas
    public static final String[] AUTHENTICATED_GET_ENDPOINTS = {
            "/swift_entry/users/**",
            "/swift_entry/events/*",
            "/swift_entry/reservations/me",
            "/swift_entry/reservations/organizer",
            "/swift_entry/payments/me",
            "/swift_entry/tickets/me"
    };
}
```

### ⚠️ El orden importa — rutas específicas antes que wildcards

Mira este fragmento de `SecurityConfig`:

```java
.requestMatchers(HttpMethod.GET, SecurityRoutes.AUTHENTICATED_GET_ENDPOINTS).authenticated()
.requestMatchers(HttpMethod.GET, SecurityRoutes.ADMIN_GET_ENDPOINTS).hasRole("ADMINISTRATOR")
```

`AUTHENTICATED_GET_ENDPOINTS` incluye `/swift_entry/reservations/me`.  
`ADMIN_GET_ENDPOINTS` incluye `/swift_entry/reservations/*`.

`/reservations/me` **encaja en ambos patrones**. Spring evalúa las reglas en orden — la primera que coincide gana. Si las declaráramos al revés (admin primero), `/reservations/me` sería absorbida por `/reservations/*` y un usuario normal recibiría un `403`, aunque tenga sesión. **Las rutas específicas siempre van antes que los wildcards.**

---

## Paso 8 — Refresh Tokens: mantener la sesión viva

### El modelo en la base de datos

```java
// security/auth/models/RefreshTokenModel.java
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 36)
    private String token;  // UUID como "a3f8b2c1-4d5e-6789-abcd-ef0123456789"

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;  // now + 7 días

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

### Crear un refresh token

```java
// security/auth/services/RefreshTokenServiceImpl.java
@Override
@Transactional
public RefreshTokenModel createRefreshToken(String userEmail) {
    UserModel user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

    RefreshTokenModel refreshToken = RefreshTokenModel.builder()
            .token(UUID.randomUUID().toString())   // UUID aleatorio como token
            .user(user)
            .expiresAt(LocalDateTime.now().plusDays(refreshExpirationDays))  // 7 días
            .build();

    return refreshTokenRepository.save(refreshToken);
}
```

### Validar y rotar (el patrón más importante)

```java
// security/auth/services/AuthServiceImpl.java
@Override
public AuthResponseDTO refresh(RefreshRequestDTO refreshRequestDTO) {
    // 1. Buscar en la BD — si no existe, ya fue revocado o nunca existió
    RefreshTokenModel oldToken = refreshTokenService.validateRefreshToken(
            refreshRequestDTO.getRefreshToken()
    );
    String userEmail = oldToken.getUser().getEmail();

    // 2. REVOCAR el token viejo inmediatamente
    refreshTokenService.revokeToken(refreshRequestDTO.getRefreshToken());

    // 3. Generar un access token nuevo
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
    String accessToken = jwtService.generateToken(userDetails);

    // 4. Generar un refresh token nuevo (rotación)
    RefreshTokenModel newRefreshToken = refreshTokenService.createRefreshToken(userEmail);

    return AuthResponseDTO.builder()
            .accessToken(accessToken)
            .refreshToken(newRefreshToken.getToken())  // el cliente guarda este nuevo token
            .tokenType("Bearer")
            .role(...)
            .build();
}
```

**¿Por qué revocar el token antes de emitir uno nuevo?** Este patrón se llama **rotación de refresh tokens**. Cada vez que se usa un refresh token, se destruye y se crea uno nuevo. Si alguien roba el token y lo usa, el usuario real recibirá un error la próxima vez que intente refrescar (porque su token ya no existe). Esa anomalía es detectable.

### Validar el refresh token

```java
@Override
public RefreshTokenModel validateRefreshToken(String token) {
    RefreshTokenModel refreshToken = refreshTokenRepository.findByTokenWithUser(token)
            .orElseThrow(() -> new InvalidTokenException("Refresh token not found or already revoked"));

    if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
        refreshTokenRepository.delete(refreshToken);  // limpiar el token expirado
        throw new InvalidTokenException("Refresh token has expired");
    }

    return refreshToken;
}
```

### Logout

```java
@Override
public void logout(RefreshRequestDTO refreshRequestDTO) {
    refreshTokenService.revokeToken(refreshRequestDTO.getRefreshToken());
}

// RefreshTokenServiceImpl
@Override
@Transactional
public void revokeToken(String token) {
    refreshTokenRepository.deleteByToken(token);  // borrarlo de la BD
}
```

Cerrar sesión simplemente borra el refresh token de la base de datos. El access token técnicamente sigue siendo válido hasta que expire (máximo 15 minutos). No hay lista negra de access tokens — es un trade-off aceptado en este tipo de arquitecturas.

---

## Paso 9 — Manejo de errores de seguridad

Spring Security tiene dos puntos de error distintos para situaciones de seguridad, y los manejamos con JSON limpio en vez del HTML por defecto.

### Error 401 — No autenticado (`AuthEntryPoint`)

Se activa cuando una petición llega a una ruta protegida **sin token**, o cuando el filtro no estableció ninguna autenticación.

```java
// security/handler/AuthEntryPoint.java
@Component
@RequiredArgsConstructor
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        GeneralResponse body = GeneralResponse.builder()
                .uri(request.getRequestURI())
                .message("Authentication required: please provide a valid token")
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
```

### Error 403 — No autorizado (`AppAccessDeniedHandler`)

Se activa cuando el usuario **sí tiene token válido** pero su rol no tiene permiso para esa ruta (ej. un `CUSTOMER` intentando acceder a `/users`).

```java
// security/handler/AppAccessDeniedHandler.java
@Component
@RequiredArgsConstructor
public class AppAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        GeneralResponse body = GeneralResponse.builder()
                .uri(request.getRequestURI())
                .message("Access denied: you do not have the required role for this resource")
                .status(HttpStatus.FORBIDDEN.value())
                .build();

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
```

### Error 401 dentro del filtro (token expirado/malformado)

El `JwtAuthenticationFilter` maneja este caso directamente, antes de que la cadena llegue a `AuthEntryPoint`:

```java
// En JwtAuthenticationFilter.doFilterInternal
try {
    userEmail = jwtService.extractUsername(jwt);
} catch (ExpiredJwtException e) {
    sendUnauthorizedResponse(response, request, "Access token has expired");
    return;  // cortar la cadena aquí
} catch (JwtException e) {
    sendUnauthorizedResponse(response, request, "Invalid or malformed token");
    return;
}
```

### Mapa de los tres caminos de error

| Situación | Código | Quién responde |
|---|---|---|
| Token expirado o malformado | `401` | `JwtAuthenticationFilter` (directo) |
| Ruta protegida sin token | `401` | `AuthEntryPoint` |
| Token válido pero rol insuficiente | `403` | `AppAccessDeniedHandler` |
| Token de refresco inválido/expirado | `401` | `GlobalExceptionHandler` vía `InvalidTokenException` |

---

## El sistema completo: de login a petición protegida

```mermaid
sequenceDiagram
    participant C as Cliente
    participant F as JwtAuthFilter
    participant SC as SecurityContext
    participant CTRL as Controlador
    participant S as Servicio

    Note over C: --- PRIMER LOGIN ---
    C->>+CTRL: POST /auth/login { email, password }
    CTRL-->>-C: { accessToken, refreshToken, user }

    Note over C: --- PETICIÓN PROTEGIDA ---
    C->>F: GET /reservations/me\nAuthorization: Bearer <accessToken>
    F->>F: Extraer y verificar JWT
    F->>SC: setAuthentication(userDetails + roles)
    F->>CTRL: Pasar petición al controlador
    CTRL->>S: getMyReservations( authentication.getName() )
    S-->>CTRL: Lista de reservas
    CTRL-->>C: 200 OK { data: [...] }

    Note over C: --- TOKEN EXPIRADO (15 min después) ---
    C->>F: GET /reservations/me\nAuthorization: Bearer <accessTokenExpirado>
    F-->>C: 401 "Access token has expired"
    C->>CTRL: POST /auth/refresh { refreshToken }
    CTRL-->>C: { nuevoAccessToken, nuevoRefreshToken }
```

---

## Notas de estado del código

- 🔴 **Secreto JWT hardcodeado** en `application.yaml` — en producción debe vivir en una variable de entorno (`JWT_SECRET_KEY`), nunca en el repositorio.
- 🟡 **Sin revocación de access tokens** — al hacer logout, el access token sigue válido hasta expirar (≤15 min). Es el trade-off estándar de JWT sin lista negra.
- 🟡 **`spring.security.user`** con `name: mike` / `password: holly` en `application.yaml` — es un default sin uso que debería eliminarse.
- 🟢 **Rotación de refresh tokens** — cada uso genera un token nuevo. Buena práctica implementada correctamente.
- 🟢 **BCrypt con factor 12** — configuración robusta para contraseñas.
- 🟢 **Errores de seguridad devuelven JSON**, no el HTML de Spring por defecto — el frontend puede parsearlos directamente.

---

## Ver también
- [[Infraestructura Compartida]] — el `GlobalExceptionHandler` y el sobre `GeneralResponse`
- [[Usuario]] — la entidad que se autentica
- [[Rol]] — el nivel de permiso incrustado en el JWT
- [[Vision General del Sistema]] — dónde encaja la seguridad en las capas
