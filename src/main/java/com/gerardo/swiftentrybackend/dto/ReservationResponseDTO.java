package com.gerardo.swiftentrybackend.dto;

import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {

    private Integer id;

    private Integer userId;
    private String userName;
    private String userEmail;

    private ReservationStatus status;

    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private LocalDateTime reservedAt;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;

//    private List<ReservationSeatResponseDTO> reservationSeats;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
