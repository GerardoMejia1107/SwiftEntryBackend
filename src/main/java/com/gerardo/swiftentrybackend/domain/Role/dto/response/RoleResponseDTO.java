package com.gerardo.swiftentrybackend.domain.Role.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Representación de un rol devuelta por la API
@Builder
@Schema(description = "Representación de un rol devuelta por la API")
public class RoleResponseDTO {
    @Schema(description = "ID del rol", example = "2")
    private Integer id;
    @Schema(description = "Nombre único del rol", example = "ADMINISTRATOR")
    private String name;
    @Schema(description = "Descripción del rol", example = "Administrador con acceso total al sistema")
    private String description;
    @Schema(description = "Fecha y hora de creación del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha y hora de la última actualización del registro", example = "2026-02-01T08:12:00")
    private LocalDateTime updatedAt;
}
