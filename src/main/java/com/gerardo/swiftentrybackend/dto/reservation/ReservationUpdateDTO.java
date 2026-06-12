package com.gerardo.swiftentrybackend.dto.reservation;

import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationUpdateDTO {

    private ReservationStatus status;

    @DecimalMin(value = "0.00", message = "Discount amount cannot be negative")
    private BigDecimal discountAmount;

    private LocalDateTime purchasedAt;
}
