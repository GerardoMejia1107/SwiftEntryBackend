package com.gerardo.swiftentrybackend.domain.Seat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para asignar una lista de asientos físicos (identificados como fila+columna, ej. \"A1\") a una localidad")
// Datos para asignar una lista de asientos físicos (identificados como fila+columna, ej. "A1") a una localidad.
public class SeatAssignmentRequestDTO {

    @Schema(description = "ID de la localidad destino", example = "1")
    @NotNull(message = "Locality id is required")
    private Long localityId;

    @Schema(description = "Identificadores de los asientos físicos a asignar (fila + columna)", example = "[\"A1\", \"A2\", \"A3\"]")
    @NotEmpty(message = "At least one seat must be selected")
    private List<String> seats;
}
