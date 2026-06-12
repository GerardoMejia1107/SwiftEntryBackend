package com.gerardo.swiftentrybackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationSeatRequestDTO {

    @NotNull(message = "Seat id is required")
    private Integer seatId;
}
