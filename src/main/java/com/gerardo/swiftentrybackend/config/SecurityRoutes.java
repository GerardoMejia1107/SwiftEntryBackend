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

    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/swift_entry/users", "/swift_entry/events", "/swift_entry/roles"
    };

    public static final String[] AUTHENTICATED_GET_ENDPOINTS = {
            "/swift_entry/users/**"
    };
}
