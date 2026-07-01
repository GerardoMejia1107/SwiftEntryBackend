package com.gerardo.swiftentrybackend.config;

import lombok.Data;

// Listas de rutas usadas por SecurityConfig para definir reglas de acceso público/autenticado/admin
@Data
public final class SecurityRoutes {
    // Rutas GET públicas (sin autenticación) más docs de Swagger
    public static final String[] PUBLIC_ENDPOINTS = {
            "/swift_entry/auth/**", "/swift_entry/events",
            "/swift_entry/localities", "/swift_entry/localities/**",
            "/swift_entry/seats", "/swift_entry/seats/**",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
    };

    // Rutas POST públicas (registro de usuario y creación de evento)
    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/swift_entry/users", "/swift_entry/events"
    };

    // GET /reservations and GET /reservations/{id} are admin-only
    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/swift_entry/users", "/swift_entry/events", "/swift_entry/roles",
            "/swift_entry/reservations", "/swift_entry/reservations/*",
            "/swift_entry/refunds", "/swift_entry/refunds/*",
            "/swift_entry/waiting-list", "/swift_entry/waiting-list/**",
            "/swift_entry/reports/**"
    };

    // GET /reservations/me, /payments/me, /tickets/me and /reservations/organizer are available to any authenticated user
    // Specific routes must be declared BEFORE the wildcard admin routes above
    public static final String[] AUTHENTICATED_GET_ENDPOINTS = {
            "/swift_entry/users/{id}",
            "/swift_entry/events/*",
            "/swift_entry/reservations/me",
            "/swift_entry/reservations/organizer",
            "/swift_entry/payments/me",
            "/swift_entry/tickets/me",
            "/swift_entry/refunds/payment/**",
            "/swift_entry/waiting-list/me",
            "/swift_entry/notifications/me",
            "/swift_entry/notifications/me/**"
    };
}
