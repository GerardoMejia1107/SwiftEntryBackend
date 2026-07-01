package com.gerardo.swiftentrybackend.domain.Seat.dto.response;

import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Representa un asiento en el mapa visual del venue para un evento, con su localidad y estado (null si no está asignado).
public class SeatMapResponseDTO {
    private Long seatId;
    private String row;
    private String col;
    private Long localitySeatId;
    private Long localityId;
    private String localityName;
    private SeatStatus status;
}
