package com.gerardo.swiftentrybackend.domain.Payment.dto.request;

import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentUpdateDTO {

    private PaymentStatus status;

    @Size(max = 150, message = "Transaction reference cannot exceed 150 characters")
    private String transactionReference;

    private LocalDateTime paidAt;
}
