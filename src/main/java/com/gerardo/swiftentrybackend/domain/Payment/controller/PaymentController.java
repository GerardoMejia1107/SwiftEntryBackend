package com.gerardo.swiftentrybackend.domain.Payment.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Payment.dto.request.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Payment.dto.response.PaymentResponseDTO;
import com.gerardo.swiftentrybackend.domain.Payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints para procesar el pago de una reserva y consultar el historial de pagos del usuario
@Tag(name = "Pagos", description = "Procesar el pago de una reserva y consultar historial. Todos los endpoints requieren JWT")
@RestController
@RequestMapping("/swift_entry/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ResponseBuilder responseBuilder;

    // Procesa el pago de la reserva del usuario autenticado
    @Operation(summary = "Procesar pago",
            description = "Convierte una reserva PENDING en CONFIRMED, marca los asientos como SOLD y emite un Ticket por cada asiento. Incluye IVA del 13%. " +
                    "Nota de implementación: PaymentServiceImpl.processPayment NO es @Transactional a propósito — si la reserva ya expiró, " +
                    "primero persiste el estado EXPIRED y luego lanza 400, por lo que ese cambio de estado queda confirmado en BD aunque la " +
                    "llamada de pago falle. La mutación atómica de éxito (pago APPROVED, asientos SOLD, tickets emitidos) corre en la transacción " +
                    "separada de PaymentExecutor.execute, que revalida el estado y la expiración dentro de la misma transacción")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pago registrado (APPROVED con tickets emitidos, o FAILED si la pasarela simulada rechaza el intento)",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, o la reserva ya expiró (se marca EXPIRED antes de responder)"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "La reserva no pertenece al usuario autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario o reserva no encontrados"),
            @ApiResponse(responseCode = "409", description = "La reserva no está en estado PENDING (ya fue pagada, cancelada o expirada), " +
                    "incluida la revalidación concurrente dentro de PaymentExecutor")
    })
    @PostMapping
    public ResponseEntity<GeneralResponse> processPayment(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Reserva a pagar y método de pago", required = true)
            PaymentRequestDTO request,
            Authentication authentication
    ) {
        PaymentResponseDTO response = paymentService.processPayment(
                request, authentication.getName());
        return responseBuilder.buildResponse(
                "Payment processed successfully", HttpStatus.CREATED, response);
    }

    // Devuelve el historial de pagos del usuario autenticado
    @Operation(summary = "Mis pagos", description = "Lista el historial de pagos del usuario autenticado, incluyendo los tickets emitidos en cada pago aprobado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagos obtenidos exitosamente",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyPayments(Authentication authentication) {
        List<PaymentResponseDTO> response = paymentService.getMyPayments(authentication.getName());
        return responseBuilder.buildResponse(
                "Payments retrieved successfully", HttpStatus.OK, response);
    }
}
