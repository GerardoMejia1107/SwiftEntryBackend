package com.gerardo.swiftentrybackend.domain.Seat.dto.response;

import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalitySeatResponseDTO {
    private Long localitySeatId;
    private Long seatId;
    private String seatNumber;
    private String rowLabel;
    private Long localityId;
    private String localityName;
    private SeatStatus status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
