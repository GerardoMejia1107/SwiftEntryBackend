package com.gerardo.swiftentrybackend.domain.Payment.utils;

import com.gerardo.swiftentrybackend.domain.Payment.PaymentModel;
import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Payment.dto.request.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Payment.dto.response.PaymentResponseDTO;
import com.gerardo.swiftentrybackend.domain.Payment.dto.request.PaymentUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Conversión entre PaymentModel y sus DTOs de entrada/salida
@Component
public class PaymentMapper {

    // Construye un PaymentModel nuevo a partir de la solicitud, sin id de persistencia
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
                .paidAt(paidAt)
                .build();
    }

    // Mapea sin tickets (usado cuando el pago no fue aprobado)
    public PaymentResponseDTO toResponse(PaymentModel model) {
        return toResponse(model, List.of());
    }

    // Mapea incluyendo los tickets emitidos para el pago
    public PaymentResponseDTO toResponse(PaymentModel model, List<TicketResponseDTO> tickets) {
        return PaymentResponseDTO.builder()
                .id(model.getId())
                .reservationId(model.getReservation()
                        .getId())
                .amount(model.getAmount())
                .paymentMethod(model.getPaymentMethod())
                .status(model.getStatus())
                .reservationStatus(model.getReservation()
                        .getStatus())
                .transactionReference(model.getTransactionReference())
                .paidAt(model.getPaidAt())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .tickets(tickets)
                .build();
    }

    public List<PaymentResponseDTO> toResponseList(List<PaymentModel> payments) {
        return payments.stream()
                .map(this::toResponse)
                .toList();
    }

    // Aplica al modelo solo los campos no nulos presentes en el DTO de actualización
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
