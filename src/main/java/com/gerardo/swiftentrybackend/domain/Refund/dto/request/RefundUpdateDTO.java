package com.gerardo.swiftentrybackend.domain.Refund.dto.request;

import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos para actualizar parcialmente un reembolso (estado, motivo, fecha de reembolso).
@Schema(description = "Datos para actualizar parcialmente un reembolso (estado, motivo, fecha de reembolso)")
public class RefundUpdateDTO {

    @Schema(description = "Nuevo estado del reembolso", example = "COMPLETED")
    private RefundStatus status;

    @Schema(description = "Motivo o nota administrativa del reembolso", example = "Aprobado por soporte")
    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;

    @Schema(description = "Fecha y hora en la que se efectuó el reembolso")
    private LocalDateTime refundedAt;
}
