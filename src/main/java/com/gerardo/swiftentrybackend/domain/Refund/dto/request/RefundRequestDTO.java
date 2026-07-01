package com.gerardo.swiftentrybackend.domain.Refund.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos para solicitar un reembolso: pago, monto y motivo.
@Schema(description = "Datos para solicitar un reembolso: pago, monto y motivo")
public class RefundRequestDTO {

    @Schema(description = "Id del pago aprobado sobre el cual se solicita el reembolso", example = "10")
    @NotNull(message = "Payment id is required")
    private Integer paymentId;

    @Schema(description = "Monto a reembolsar; no puede exceder el monto original del pago", example = "150.00")
    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than zero")
    private BigDecimal amount;

    @Schema(description = "Motivo de la solicitud de reembolso", example = "No puedo asistir al evento")
    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;
}
