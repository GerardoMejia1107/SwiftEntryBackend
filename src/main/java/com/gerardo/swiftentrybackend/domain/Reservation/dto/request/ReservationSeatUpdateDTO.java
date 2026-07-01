package com.gerardo.swiftentrybackend.domain.Reservation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Campo opcional para actualizar el precio congelado de un asiento reservado
public class ReservationSeatUpdateDTO {

    @DecimalMin(value = "0.00", message = "Price at reservation cannot be negative")
    private BigDecimal priceAtReservation;
}