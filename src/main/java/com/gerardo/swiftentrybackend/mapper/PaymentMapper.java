package com.gerardo.swiftentrybackend.mapper;

import com.gerardo.swiftentrybackend.domain.Payment.PaymentModel;
import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.dto.payment.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.dto.payment.PaymentResponseDTO;
import com.gerardo.swiftentrybackend.dto.payment.PaymentUpdateDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentMapper {

    public PaymentModel toModel(
            PaymentRequestDTO dto,
            ReservationModel reservation,
            BigDecimal amount,
            PaymentStatus status,
            LocalDateTime paidAt
    ) {
        return PaymentModel.builder()
                .reservation(reservation)
                .amount(amount)
                .paymentMethod(dto.getPaymentMethod())
                .status(status)
                .transactionReference(dto.getTransactionReference())
                .paidAt(paidAt)
                .build();
    }

    public PaymentResponseDTO toResponse(PaymentModel model) {
        return PaymentResponseDTO.builder()
                .id(model.getId())
                .reservationId(model.getReservation().getId())
                .amount(model.getAmount())
                .paymentMethod(model.getPaymentMethod())
                .status(model.getStatus())
                .transactionReference(model.getTransactionReference())
                .paidAt(model.getPaidAt())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    public List<PaymentResponseDTO> toResponseList(List<PaymentModel> payments) {
        return payments.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateModel(PaymentModel model, PaymentUpdateDTO dto) {
        if (dto.getStatus() != null) {
            model.setStatus(dto.getStatus());
        }

        if (dto.getTransactionReference() != null) {
            model.setTransactionReference(dto.getTransactionReference());
        }

        if (dto.getPaidAt() != null) {
            model.setPaidAt(dto.getPaidAt());
        }
    }
}
