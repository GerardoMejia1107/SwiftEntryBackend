package com.gerardo.swiftentrybackend.domain.Seat.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos para asignar una lista de asientos físicos (identificados como fila+columna, ej. "A1") a una localidad.
public class SeatAssignmentRequestDTO {

    @NotNull(message = "Locality id is required")
    private Long localityId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<String> seats;
}
