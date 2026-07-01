package com.gerardo.swiftentrybackend.domain.Address.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Datos de entrada para crear o actualizar una dirección
@Builder
@Schema(description = "Datos de entrada para crear o actualizar una dirección")
public class AddressRequestDTO {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Calle y número", example = "Calle El Progreso #123")
    private String streetAddress;

    @Size(max = 100)
    @Schema(description = "Colonia o barrio", example = "Colonia Escalón")
    private String neighborhood;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Municipio", example = "San Salvador")
    private String municipality;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Departamento", example = "San Salvador")
    private String department;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "País", example = "El Salvador")
    private String country;

    @Size(max = 255)
    @Schema(description = "Punto de referencia adicional", example = "Frente al parque central")
    private String referencePoint;
}