package com.gerardo.swiftentrybackend.domain.Reservation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDTO {
    @NotNull(message = "User id is required")
    private Integer userId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<Long> seatIds;
}
