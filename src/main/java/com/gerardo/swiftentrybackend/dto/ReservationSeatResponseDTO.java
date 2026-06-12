package com.gerardo.swiftentrybackend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationSeatResponseDTO {

    private Integer id;

    private Integer reservationId;

    private Integer seatId;
    private String seatNumber;
    private String rowLabel;

    private Integer localityId;
    private String localityName;

    private BigDecimal priceAtReservation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
