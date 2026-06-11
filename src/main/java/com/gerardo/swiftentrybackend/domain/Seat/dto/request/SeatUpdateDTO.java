package com.gerardo.swiftentrybackend.domain.Seat.dto.request;

import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatUpdateDTO {

    @Size(max = 20, message = "Seat number cannot exceed 20 characters")
    private String seatNumber;

    @Size(max = 10, message = "Row label cannot exceed 10 characters")
    private String rowLabel;

    private SeatStatus status;

    @Size(max = 64, message = "QR hash cannot exceed 64 characters")
    private String qrHash;

    private Boolean isActive;
}
