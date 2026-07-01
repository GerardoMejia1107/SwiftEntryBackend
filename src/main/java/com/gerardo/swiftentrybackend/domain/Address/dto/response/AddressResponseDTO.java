package com.gerardo.swiftentrybackend.domain.Address.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Representación de una dirección devuelta por la API
@Builder
@Schema(description = "Representación de una dirección devuelta por la API")
public class AddressResponseDTO {

    @Schema(description = "ID de la dirección", example = "3")
    private Integer id;
    @Schema(description = "Calle y número", example = "Calle El Progreso #123")
    private String streetAddress;
    @Schema(description = "Colonia o barrio", example = "Colonia Escalón")
    private String neighborhood;
    @Schema(description = "Municipio", example = "San Salvador")
    private String municipality;
    @Schema(description = "Departamento", example = "San Salvador")
    private String department;
    @Schema(description = "País", example = "El Salvador")
    private String country;
    @Schema(description = "Punto de referencia adicional", example = "Frente al parque central")
    private String referencePoint;
    @Schema(description = "Fecha y hora de creación del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha y hora de la última actualización del registro", example = "2026-02-01T08:12:00")
    private LocalDateTime updatedAt;
}