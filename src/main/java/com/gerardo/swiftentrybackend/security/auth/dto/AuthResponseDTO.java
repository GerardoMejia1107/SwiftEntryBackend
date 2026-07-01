package com.gerardo.swiftentrybackend.security.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Respuesta de login/refresh: tokens emitidos y datos básicos del usuario autenticado
@Builder
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String role;
    private AuthUserDTO user;
}
