package com.gerardo.swiftentrybackend.config;

import lombok.Data;

@Data
public final class SecurityRoutes {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/swift_entry/auth/**", "/swift_entry/events",
            "/swift_entry/localities", "/swift_entry/localities/**",
            "/swift_entry/seats", "/swift_entry/seats/**"
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/swift_entry/users", "/swift_entry/events"
    };

    // GET /reservations and GET /reservations/{id} are admin-only
    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/swift_entry/users", "/swift_entry/events", "/swift_entry/roles",
            "/swift_entry/reservations", "/swift_entry/reservations/*",
            "/swift_entry/refunds", "/swift_entry/refunds/*",
            "/swift_entry/waiting-list", "/swift_entry/waiting-list/**"
    };

    // GET /reservations/me, /payments/me, /tickets/me and /reservations/organizer are available to any authenticated user
    // Specific routes must be declared BEFORE the wildcard admin routes above
    public static final String[] AUTHENTICATED_GET_ENDPOINTS = {
            "/swift_entry/users/**",
            "/swift_entry/events/*",
            "/swift_entry/reservations/me",
            "/swift_entry/reservations/organizer",
            "/swift_entry/payments/me",
            "/swift_entry/tickets/me",
            "/swift_entry/refunds/payment/**",
            "/swift_entry/waiting-list/me"
    };
}
