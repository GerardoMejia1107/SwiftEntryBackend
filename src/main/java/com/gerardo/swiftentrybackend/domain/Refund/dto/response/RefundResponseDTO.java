package com.gerardo.swiftentrybackend.domain.Refund.dto.response;

import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Representación de un reembolso para respuestas de API.
@Schema(description = "Representación de un reembolso para respuestas de API")
public class RefundResponseDTO {

    @Schema(description = "Id del reembolso", example = "1")
    private Integer id;

    @Schema(description = "Id del pago reembolsado", example = "10")
    private Integer paymentId;
    @Schema(description = "Id de la reserva asociada al pago reembolsado", example = "5")
    private Integer reservationId;

    @Schema(description = "Monto reembolsado", example = "150.00")
    private BigDecimal amount;

    @Schema(description = "Motivo del reembolso", example = "No puedo asistir al evento")
    private String reason;

    @Schema(description = "Estado actual del reembolso", example = "REQUESTED")
    private RefundStatus status;

    @Schema(description = "Fecha y hora en la que se efectuó el reembolso")
    private LocalDateTime refundedAt;

    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha de última actualización del registro")
    private LocalDateTime updatedAt;
}
