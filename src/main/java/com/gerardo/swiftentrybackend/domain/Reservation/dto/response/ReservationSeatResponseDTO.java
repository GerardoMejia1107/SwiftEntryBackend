package com.gerardo.swiftentrybackend.domain.Reservation.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Representación de un asiento dentro de una reserva, con su ubicación y precio congelado
public class ReservationSeatResponseDTO {

    private Integer id;

    private Integer reservationId;

    private Long seatId;
    private String seatNumber;
    private String rowLabel;

    private Long localityId;
    private String localityName;

    private BigDecimal priceAtReservation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
