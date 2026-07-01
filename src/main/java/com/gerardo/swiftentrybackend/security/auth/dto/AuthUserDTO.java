package com.gerardo.swiftentrybackend.security.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Datos mínimos del usuario embebidos en AuthResponseDTO
@Builder
@Schema(description = "Datos mínimos del usuario embebidos en AuthResponseDTO")
public class AuthUserDTO {
    @Schema(description = "ID del usuario", example = "1")
    private Integer id;
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;
    @Schema(description = "Correo electrónico del usuario", example = "usuario@ejemplo.com")
    private String email;
    @Schema(description = "Rol del usuario autenticado", example = "ADMINISTRATOR")
    private String role;
}
