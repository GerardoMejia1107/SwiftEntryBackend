package com.gerardo.swiftentrybackend.domain.Role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Datos de entrada para crear un rol
@Builder
@Schema(description = "Datos de entrada para crear un rol")
public class RoleRequestDTO {
    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name cannot exceed 50 characters")
    @Schema(description = "Nombre único del rol", example = "ADMINISTRATOR")
    private String name;

    @NotBlank(message = "Role description is required")
    @Size(max = 250, message = "Role description cannot exceed 250 characters")
    @Schema(description = "Descripción del rol", example = "Administrador con acceso total al sistema")
    private String description;
}
