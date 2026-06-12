package com.gerardo.swiftentrybackend.domain.Payment.dto.response;

import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentMethod;
import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private Integer id;

    private Integer reservationId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;
    private PaymentStatus status;

    private String transactionReference;

    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
