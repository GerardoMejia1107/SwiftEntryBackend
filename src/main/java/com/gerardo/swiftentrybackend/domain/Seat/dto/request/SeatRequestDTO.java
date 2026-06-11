package com.gerardo.swiftentrybackend.domain.Seat.dto.request;

import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatRequestDTO {

    @NotNull(message = "Locality id is required")
    private Long localityId;

    @NotBlank(message = "Seat number is required")
    @Size(max = 20, message = "Seat number cannot exceed 20 characters")
    private String seatNumber;

    @NotBlank(message = "Row label is required")
    @Size(max = 10, message = "Row label cannot exceed 10 characters")
    private String rowLabel;

    private SeatStatus status;

    @Size(max = 64, message = "QR hash cannot exceed 64 characters")
    private String qrHash;
}
