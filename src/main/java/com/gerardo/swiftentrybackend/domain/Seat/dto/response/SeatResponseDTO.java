package com.gerardo.swiftentrybackend.domain.Seat.dto.response;

import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponseDTO {

    private Long id;

    private Long localityId;

    private String localityName;

    private String seatNumber;

    private String rowLabel;

    private SeatStatus status;

    private Boolean isActive;

    private String qrHash;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
