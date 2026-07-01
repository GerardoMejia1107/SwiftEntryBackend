package com.gerardo.swiftentrybackend.security.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Datos de entrada para renovar sesión o cerrar sesión (contiene el refresh token)
@Builder
@Schema(description = "Datos de entrada para renovar sesión o cerrar sesión")
public class RefreshRequestDTO {
    @NotBlank
    @Schema(description = "Refresh token opaco (UUID) emitido en el login", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private String refreshToken;
}
