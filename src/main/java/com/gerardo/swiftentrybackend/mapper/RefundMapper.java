package com.gerardo.swiftentrybackend.mapper;

import com.gerardo.swiftentrybackend.domain.Payment.PaymentModel;
import com.gerardo.swiftentrybackend.domain.Refund.RefundModel;
import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import com.gerardo.swiftentrybackend.dto.refund.RefundRequestDTO;
import com.gerardo.swiftentrybackend.dto.refund.RefundResponseDTO;
import com.gerardo.swiftentrybackend.dto.refund.RefundUpdateDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RefundMapper {

    public RefundModel toModel(
            RefundRequestDTO dto,
            PaymentModel payment,
            RefundStatus status,
            LocalDateTime refundedAt
    ) {
        return RefundModel.builder()
                .payment(payment)
                .amount(dto.getAmount())
                .reason(dto.getReason())
                .status(status)
                .refundedAt(refundedAt)
                .build();
    }

    public RefundResponseDTO toResponse(RefundModel model) {
        return RefundResponseDTO.builder()
                .id(model.getId())
                .paymentId(model.getPayment().getId())
                .reservationId(model.getPayment().getReservation().getId())
                .amount(model.getAmount())
                .reason(model.getReason())
                .status(model.getStatus())
                .refundedAt(model.getRefundedAt())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    public List<RefundResponseDTO> toResponseList(List<RefundModel> refunds) {
        return refunds.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateModel(RefundModel model, RefundUpdateDTO dto) {
        if (dto.getStatus() != null) {
            model.setStatus(dto.getStatus());
        }

        if (dto.getReason() != null) {
            model.setReason(dto.getReason());
        }

        if (dto.getRefundedAt() != null) {
            model.setRefundedAt(dto.getRefundedAt());
        }
    }
}
