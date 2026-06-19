package com.gerardo.swiftentrybackend.domain.Payment.service;

import com.gerardo.swiftentrybackend.domain.Payment.dto.request.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Payment.dto.response.PaymentResponseDTO;

public interface PaymentService {

    /**
     * Processes a payment for an existing reservation owned by the authenticated user.
     * On success the reservation is confirmed, its seats are marked as SOLD and a
     * ticket is issued per seat.
     */
    PaymentResponseDTO processPayment(PaymentRequestDTO requestDTO, String userEmail);
}
