package com.gerardo.swiftentrybackend.domain.Refund.dto.request;

import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos para actualizar parcialmente un reembolso (estado, motivo, fecha de reembolso).
public class RefundUpdateDTO {

    private RefundStatus status;

    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;

    private LocalDateTime refundedAt;
}
