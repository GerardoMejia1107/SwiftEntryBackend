package com.gerardo.swiftentrybackend.domain.Reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Identifica el asiento físico a incluir en una reserva
public class ReservationSeatRequestDTO {

    @NotNull(message = "Seat id is required")
    private Integer seatId;
}
