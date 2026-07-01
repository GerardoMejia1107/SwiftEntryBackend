package com.gerardo.swiftentrybackend.domain.Payment.dto.request;

import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Campos opcionales para actualizar un pago existente
// PENDIENTE: DTO no utilizado actualmente por ningún endpoint (solo referenciado por PaymentMapper.updateModel,
// que a su vez no es invocado desde ningún service/controller)
@Schema(description = "Campos opcionales para actualizar un pago existente. " +
        "PENDIENTE: DTO no utilizado actualmente por ningún endpoint")
public class PaymentUpdateDTO {

    @Schema(description = "Nuevo estado del pago", example = "APPROVED")
    private PaymentStatus status;

    @Size(max = 150, message = "Transaction reference cannot exceed 150 characters")
    @Schema(description = "Referencia de la transacción en la pasarela de pago", example = "TXN-3f8a1c2e")
    private String transactionReference;

    @Schema(description = "Fecha y hora en que se confirmó el pago")
    private LocalDateTime paidAt;
}
