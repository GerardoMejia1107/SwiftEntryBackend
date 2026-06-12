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
public class RefundUpdateDTO {

    private RefundStatus status;

    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;

    private LocalDateTime refundedAt;
}
