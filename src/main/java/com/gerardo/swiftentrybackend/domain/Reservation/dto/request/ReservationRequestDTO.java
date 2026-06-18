package com.gerardo.swiftentrybackend.domain.Reservation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDTO {

    @NotEmpty(message = "At least one seat must be selected")
    @Size(max = 5, message = "Cannot reserve more than 5 seats at a time")
    private List<Long> localitySeatIds;
}
