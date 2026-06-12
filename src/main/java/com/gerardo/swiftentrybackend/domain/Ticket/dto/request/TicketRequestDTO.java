package com.gerardo.swiftentrybackend.domain.Ticket.dto.request;

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
