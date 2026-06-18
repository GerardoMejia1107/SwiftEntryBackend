package com.gerardo.swiftentrybackend.domain.Seat.dto.response;

import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatMapResponseDTO {
    private Long seatId;
    private String row;
    private String col;
    private Long localitySeatId;
    private Long localityId;
    private String localityName;
    private SeatStatus status;
}
