package com.gerardo.swiftentrybackend.domain.Payment.service;

import com.gerardo.swiftentrybackend.domain.Payment.dto.request.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Payment.dto.response.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {

    PaymentResponseDTO processPayment(PaymentRequestDTO requestDTO, String userEmail);

    List<PaymentResponseDTO> getMyPayments(String userEmail);
}
