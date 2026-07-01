package com.gerardo.swiftentrybackend.domain.Reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Identifica el asiento físico a incluir en una reserva
// PENDIENTE: no expuesto directamente por ningún endpoint; ReservationController usa
// ReservationRequestDTO.localitySeatIds (List<Long>) para crear reservas, no este DTO
@Schema(description = "Identifica el asiento físico a incluir en una reserva. " +
        "PENDIENTE: no expuesto directamente por ningún endpoint (ReservationRequestDTO usa una lista de IDs, no este DTO)")
public class ReservationSeatRequestDTO {

    @NotNull(message = "Seat id is required")
    @Schema(description = "ID del asiento físico (Seat)", example = "5")
    private Integer seatId;
}
