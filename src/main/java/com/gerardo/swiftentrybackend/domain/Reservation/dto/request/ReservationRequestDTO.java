package com.gerardo.swiftentrybackend.domain.Reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos para crear una reserva: los asientos seleccionados (máximo 5)
@Schema(description = "Datos para crear una reserva: los IDs de LocalitySeat seleccionados (máximo 5)")
public class ReservationRequestDTO {

    @NotEmpty(message = "At least one seat must be selected")
    @Size(max = 5, message = "Cannot reserve more than 5 seats at a time")
    @Schema(description = "IDs de los LocalitySeat (asiento dentro de una localidad) a reservar", example = "[10, 11, 12]")
    private List<Long> localitySeatIds;
}
