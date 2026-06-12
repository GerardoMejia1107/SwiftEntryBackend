package com.gerardo.swiftentrybackend.dto.refund;

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
public class RefundRequestDTO {

    @NotNull(message = "Payment id is required")
    private Integer paymentId;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than zero")
    private BigDecimal amount;

    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;
}
