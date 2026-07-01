package com.gerardo.swiftentrybackend.domain.Payment.dto.request;

import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos que envía el cliente para procesar el pago de una reserva
@Schema(description = "Datos que envía el cliente para procesar el pago de una reserva PENDING")
public class PaymentRequestDTO {

    @NotNull(message = "Reservation id is required")
    @Schema(description = "ID de la reserva a pagar (debe estar en estado PENDING y pertenecer al usuario autenticado)", example = "1")
    private Integer reservationId;

    @NotNull(message = "Payment method is required")
    @Schema(description = "Método de pago utilizado", example = "CREDIT_CARD")
    private PaymentMethod paymentMethod;
}
