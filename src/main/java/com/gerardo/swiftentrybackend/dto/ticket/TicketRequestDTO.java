package com.gerardo.swiftentrybackend.dto.ticket;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketRequestDTO {

    @NotNull(message = "Reservation id is required")
    private Integer reservationId;

    @NotNull(message = "Seat id is required")
    private Integer seatId;
}
