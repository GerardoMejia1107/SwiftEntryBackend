package com.gerardo.swiftentrybackend.domain.Payment.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Payment.dto.request.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Payment.dto.response.PaymentResponseDTO;
import com.gerardo.swiftentrybackend.domain.Payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/swift_entry/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ResponseBuilder responseBuilder;

    // POST — process a payment for the authenticated user's reservation
    @PostMapping
    public ResponseEntity<GeneralResponse> processPayment(
            @Valid @RequestBody PaymentRequestDTO request,
            Authentication authentication
    ) {
        PaymentResponseDTO response = paymentService.processPayment(
                request, authentication.getName());
        return responseBuilder.buildResponse(
                "Payment processed successfully", HttpStatus.CREATED, response);
    }
}
