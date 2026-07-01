package com.gerardo.swiftentrybackend.domain.Payment.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Payment.dto.request.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Payment.dto.response.PaymentResponseDTO;
import com.gerardo.swiftentrybackend.domain.Payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints para procesar el pago de una reserva y consultar el historial de pagos del usuario
@Tag(name = "Pagos", description = "Procesar el pago de una reserva y consultar historial")
@RestController
@RequestMapping("/swift_entry/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ResponseBuilder responseBuilder;

    // Procesa el pago de la reserva del usuario autenticado
    @Operation(summary = "Procesar pago", description = "Convierte una reserva PENDING en CONFIRMED, marca los asientos como SOLD y emite un Ticket por cada asiento. Incluye IVA del 13%")
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

    // Devuelve el historial de pagos del usuario autenticado
    @Operation(summary = "Mis pagos", description = "Lista el historial de pagos del usuario autenticado")
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyPayments(Authentication authentication) {
        List<PaymentResponseDTO> response = paymentService.getMyPayments(authentication.getName());
        return responseBuilder.buildResponse(
                "Payments retrieved successfully", HttpStatus.OK, response);
    }
}
