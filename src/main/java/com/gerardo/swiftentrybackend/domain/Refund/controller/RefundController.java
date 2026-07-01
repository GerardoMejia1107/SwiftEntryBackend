package com.gerardo.swiftentrybackend.domain.Refund.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundRequestDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.response.RefundResponseDTO;
import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import com.gerardo.swiftentrybackend.domain.Refund.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints de solicitud, consulta y gestión (aprobar/rechazar) de reembolsos.
@RestController
@RequestMapping("/swift_entry/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;
    private final ResponseBuilder responseBuilder;

    // Crea una solicitud de reembolso para el pago del usuario autenticado.
    @PostMapping
    public ResponseEntity<GeneralResponse> createRefundRequest(
            @Valid @RequestBody RefundRequestDTO request,
            Authentication authentication
    ) {
        RefundResponseDTO response = refundService.createRefundRequest(request, authentication.getName());
        return responseBuilder.buildResponse("Refund request created successfully", HttpStatus.CREATED, response);
    }

    // Lista todos los reembolsos (solo administradores).
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> getAllRefunds() {
        List<RefundResponseDTO> response = refundService.getAllRefunds();
        return responseBuilder.buildResponse("Refunds retrieved successfully", HttpStatus.OK, response);
    }

    // Obtiene un reembolso por id (solo administradores).
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> getRefundById(@PathVariable Integer id) {
        RefundResponseDTO response = refundService.getRefundById(id);
        return responseBuilder.buildResponse("Refund retrieved successfully", HttpStatus.OK, response);
    }

    // Lista los reembolsos de un pago; el servicio valida que sea el dueño del pago.
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<GeneralResponse> getRefundsByPaymentId(
            @PathVariable Integer paymentId,
            Authentication authentication
    ) {
        List<RefundResponseDTO> response = refundService.getRefundsByPaymentId(paymentId, authentication.getName());
        return responseBuilder.buildResponse("Refunds retrieved successfully", HttpStatus.OK, response);
    }

    // Lista los reembolsos filtrados por estado (solo administradores).
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> getRefundsByStatus(@PathVariable RefundStatus status) {
        List<RefundResponseDTO> response = refundService.getRefundsByStatus(status);
        return responseBuilder.buildResponse("Refunds retrieved successfully", HttpStatus.OK, response);
    }

    // Actualiza campos de un reembolso (solo administradores).
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> updateRefund(
            @PathVariable Integer id,
            @Valid @RequestBody RefundUpdateDTO request
    ) {
        RefundResponseDTO response = refundService.updateRefund(id, request);
        return responseBuilder.buildResponse("Refund updated successfully", HttpStatus.OK, response);
    }

    // Aprueba y procesa un reembolso (solo administradores).
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> approveRefund(@PathVariable Integer id) {
        RefundResponseDTO response = refundService.approveRefund(id);
        return responseBuilder.buildResponse("Refund approved and processed successfully", HttpStatus.OK, response);
    }

    // Rechaza un reembolso pendiente (solo administradores).
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> rejectRefund(@PathVariable Integer id) {
        RefundResponseDTO response = refundService.rejectRefund(id);
        return responseBuilder.buildResponse("Refund rejected successfully", HttpStatus.OK, response);
    }
}
