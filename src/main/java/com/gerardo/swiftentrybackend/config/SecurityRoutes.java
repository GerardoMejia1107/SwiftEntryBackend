package com.gerardo.swiftentrybackend.config;

import lombok.Data;

@Data
public final class SecurityRoutes {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/swift_entry/auth/**"
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/swift_entry/users"
    };

    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/swift_entry/users"
    };

    public static final String[] AUTHENTICATED_GET_ENDPOINTS = {
            "/swift_entry/users/**"
    };
}
