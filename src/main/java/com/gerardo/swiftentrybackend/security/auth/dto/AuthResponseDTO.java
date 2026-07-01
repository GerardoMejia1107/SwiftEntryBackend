package com.gerardo.swiftentrybackend.security.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Respuesta de login/refresh: tokens emitidos y datos básicos del usuario autenticado
@Builder
@Schema(description = "Respuesta de login/refresh: tokens emitidos y datos básicos del usuario autenticado")
public class AuthResponseDTO {
    @Schema(description = "Access token JWT, expira en 15 minutos", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c3VhcmlvQGVqZW1wbG8uY29tIn0.abc123")
    private String accessToken;
    @Schema(description = "Refresh token opaco (UUID), expira en 7 días; se rota en cada uso", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private String refreshToken;
    @Schema(description = "Tipo de token a usar en el header Authorization", example = "Bearer")
    private String tokenType;
    @Schema(description = "Rol del usuario autenticado", example = "ADMINISTRATOR")
    private String role;
    @Schema(description = "Datos básicos del usuario autenticado. En /auth/refresh viaja como null, solo se llena en /auth/login")
    private AuthUserDTO user;
}
