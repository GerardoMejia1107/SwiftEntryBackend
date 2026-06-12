package com.gerardo.swiftentrybackend.domain.Payment.dto.request;

import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {

    @NotNull(message = "Reservation id is required")
    private Integer reservationId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Size(max = 150, message = "Transaction reference cannot exceed 150 characters")
    private String transactionReference;
}
