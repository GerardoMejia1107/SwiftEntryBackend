package com.gerardo.swiftentrybackend.domain.Refund.utils;

import com.gerardo.swiftentrybackend.domain.Payment.PaymentModel;
import com.gerardo.swiftentrybackend.domain.Refund.RefundModel;
import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundRequestDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.response.RefundResponseDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundUpdateDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// Convierte entre RefundModel y sus DTOs de request/response.
@Component
public class RefundMapper {

    // Construye un nuevo reembolso a partir del pago y los datos de la solicitud.
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

    // Mapea el modelo a DTO, incluyendo el id de la reserva asociada al pago.
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

    // Aplica cambios parciales (solo campos no nulos) del DTO al modelo existente.
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
