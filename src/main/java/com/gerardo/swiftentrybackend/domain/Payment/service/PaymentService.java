package com.gerardo.swiftentrybackend.domain.Payment.service;

import com.gerardo.swiftentrybackend.domain.Payment.dto.request.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Payment.dto.response.PaymentResponseDTO;

import java.util.List;

// Contrato para procesar el pago de una reserva y consultar el historial de pagos
public interface PaymentService {

    // Valida y procesa el pago; delega la mutación atómica a PaymentExecutor
    PaymentResponseDTO processPayment(PaymentRequestDTO requestDTO, String userEmail);

    // Lista los pagos del usuario autenticado
    List<PaymentResponseDTO> getMyPayments(String userEmail);
}
