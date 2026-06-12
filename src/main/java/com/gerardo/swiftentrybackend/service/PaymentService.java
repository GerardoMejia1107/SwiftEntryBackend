package com.gerardo.swiftentrybackend.service;

import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import com.gerardo.swiftentrybackend.dto.payment.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.dto.payment.PaymentResponseDTO;
import com.gerardo.swiftentrybackend.dto.payment.PaymentUpdateDTO;

import java.util.List;

public interface PaymentService {

    PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO);

    List<PaymentResponseDTO> getAllPayments();

    PaymentResponseDTO getPaymentById(Integer id);

    List<PaymentResponseDTO> getPaymentsByReservationId(Integer reservationId);

    List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status);

    PaymentResponseDTO updatePayment(Integer id, PaymentUpdateDTO updateDTO);

    PaymentResponseDTO confirmPayment(Integer paymentId);

    PaymentResponseDTO rejectPayment(Integer paymentId);
}
