package com.gerardo.swiftentrybackend.dto.refund;

import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponseDTO {

    private Integer id;

    private Integer paymentId;
    private Integer reservationId;

    private BigDecimal amount;

    private String reason;

    private RefundStatus status;

    private LocalDateTime refundedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
