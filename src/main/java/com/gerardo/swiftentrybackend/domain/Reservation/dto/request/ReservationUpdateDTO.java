package com.gerardo.swiftentrybackend.domain.Reservation.dto.request;

import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Campos opcionales para actualizar una reserva existente
// PENDIENTE: ReservationMapper.updateModel(ReservationModel, ReservationUpdateDTO) existe pero no es invocado
// desde ningún service/controller; no hay endpoint PUT/PATCH de reservas expuesto actualmente
@Schema(description = "Campos opcionales para actualizar una reserva existente. " +
        "PENDIENTE: DTO no utilizado actualmente por ningún endpoint (no existe PUT/PATCH de reservas)")
public class ReservationUpdateDTO {

    @Schema(description = "Nuevo estado de la reserva", example = "CANCELLED")
    private ReservationStatus status;

    @DecimalMin(value = "0.00", message = "Discount amount cannot be negative")
    @Schema(description = "Monto de descuento aplicado a la reserva", example = "5.00")
    private BigDecimal discountAmount;

    @Schema(description = "Fecha y hora en que se completó la compra")
    private LocalDateTime purchasedAt;
}
