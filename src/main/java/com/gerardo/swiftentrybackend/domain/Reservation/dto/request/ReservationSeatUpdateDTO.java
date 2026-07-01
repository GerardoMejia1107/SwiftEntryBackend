package com.gerardo.swiftentrybackend.domain.Reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Campo opcional para actualizar el precio congelado de un asiento reservado
// PENDIENTE: usado solo en la firma de ReservationSeatService (interfaz sin implementación);
// no hay controller que exponga este DTO
@Schema(description = "Campo opcional para actualizar el precio congelado de un asiento reservado. " +
        "PENDIENTE: no expuesto directamente por ningún endpoint (ReservationSeatService no tiene implementación ni controller)")
public class ReservationSeatUpdateDTO {

    @DecimalMin(value = "0.00", message = "Price at reservation cannot be negative")
    @Schema(description = "Precio congelado del asiento al momento de la reserva", example = "45.00")
    private BigDecimal priceAtReservation;
}