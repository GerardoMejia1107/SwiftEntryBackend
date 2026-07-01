package com.gerardo.swiftentrybackend.security.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Datos de entrada para renovar sesión o cerrar sesión (contiene el refresh token)
@Builder
public class RefreshRequestDTO {
    @NotBlank
    private String refreshToken;
}
