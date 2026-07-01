package com.gerardo.swiftentrybackend.domain.Refund.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundRequestDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.response.RefundResponseDTO;
import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import com.gerardo.swiftentrybackend.domain.Refund.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints de solicitud, consulta y gestión (aprobar/rechazar) de reembolsos.
@Tag(name = "Reembolsos", description = "Solicitud, consulta y gestión (aprobar/rechazar) de reembolsos de pagos")
@RestController
@RequestMapping("/swift_entry/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;
    private final ResponseBuilder responseBuilder;

    // Crea una solicitud de reembolso para el pago del usuario autenticado.
    @Operation(summary = "Solicitar reembolso", description = "Crea una solicitud de reembolso para un pago propio. "
            + "El pago debe estar APROBADO, no debe tener ya un reembolso pendiente, la solicitud debe hacerse "
            + "al menos 48 horas antes del inicio del evento, y el monto no puede exceder lo pagado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud de reembolso creada exitosamente",
                    content = @Content(schema = @Schema(implementation = RefundResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, ventana de 48h cerrada, o monto mayor al pagado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el dueño del pago"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "409", description = "El pago no está APROBADO, o ya existe un reembolso pendiente para este pago")
    })
    @PostMapping
    public ResponseEntity<GeneralResponse> createRefundRequest(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Pago, monto y motivo del reembolso solicitado", required = true)
            RefundRequestDTO request,
            Authentication authentication
    ) {
        RefundResponseDTO response = refundService.createRefundRequest(request, authentication.getName());
        return responseBuilder.buildResponse("Refund request created successfully", HttpStatus.CREATED, response);
    }

    // Lista todos los reembolsos (solo administradores).
    @Operation(summary = "Listar todos los reembolsos", description = "Requiere rol ADMINISTRATOR. Devuelve todos los reembolsos registrados en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reembolsos obtenidos exitosamente",
                    content = @Content(schema = @Schema(implementation = RefundResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> getAllRefunds() {
        List<RefundResponseDTO> response = refundService.getAllRefunds();
        return responseBuilder.buildResponse("Refunds retrieved successfully", HttpStatus.OK, response);
    }

    // Obtiene un reembolso por id (solo administradores).
    @Operation(summary = "Obtener reembolso por id", description = "Requiere rol ADMINISTRATOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reembolso obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = RefundResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR"),
            @ApiResponse(responseCode = "404", description = "Reembolso no encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> getRefundById(
            @Parameter(description = "Id del reembolso", example = "1") @PathVariable Integer id) {
        RefundResponseDTO response = refundService.getRefundById(id);
        return responseBuilder.buildResponse("Refund retrieved successfully", HttpStatus.OK, response);
    }

    // Lista los reembolsos de un pago; el servicio valida que sea el dueño del pago.
    @Operation(summary = "Listar reembolsos de un pago", description = "Devuelve los reembolsos asociados a un pago propio del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reembolsos obtenidos exitosamente",
                    content = @Content(schema = @Schema(implementation = RefundResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el dueño del pago"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<GeneralResponse> getRefundsByPaymentId(
            @Parameter(description = "Id del pago", example = "1") @PathVariable Integer paymentId,
            Authentication authentication
    ) {
        List<RefundResponseDTO> response = refundService.getRefundsByPaymentId(paymentId, authentication.getName());
        return responseBuilder.buildResponse("Refunds retrieved successfully", HttpStatus.OK, response);
    }

    // Lista los reembolsos filtrados por estado (solo administradores).
    @Operation(summary = "Listar reembolsos por estado", description = "Requiere rol ADMINISTRATOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reembolsos obtenidos exitosamente",
                    content = @Content(schema = @Schema(implementation = RefundResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> getRefundsByStatus(
            @Parameter(description = "Estado del reembolso", example = "REQUESTED") @PathVariable RefundStatus status) {
        List<RefundResponseDTO> response = refundService.getRefundsByStatus(status);
        return responseBuilder.buildResponse("Refunds retrieved successfully", HttpStatus.OK, response);
    }

    // Actualiza campos de un reembolso (solo administradores).
    @Operation(summary = "Actualizar reembolso", description = "Requiere rol ADMINISTRATOR. Actualiza campos parciales de un reembolso (estado, motivo, fecha de reembolso)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reembolso actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = RefundResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR"),
            @ApiResponse(responseCode = "404", description = "Reembolso no encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> updateRefund(
            @Parameter(description = "Id del reembolso", example = "1") @PathVariable Integer id,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Campos a actualizar del reembolso", required = true)
            RefundUpdateDTO request
    ) {
        RefundResponseDTO response = refundService.updateRefund(id, request);
        return responseBuilder.buildResponse("Refund updated successfully", HttpStatus.OK, response);
    }

    // Aprueba y procesa un reembolso (solo administradores).
    @Operation(summary = "Aprobar reembolso", description = "Requiere rol ADMINISTRATOR. Solo reembolsos en estado REQUESTED pueden aprobarse. "
            + "Si el monto cubre el pago completo, cancela los tickets, libera los asientos y marca reserva/pago como reembolsados; "
            + "en reembolso parcial solo se completa el reembolso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reembolso aprobado y procesado exitosamente",
                    content = @Content(schema = @Schema(implementation = RefundResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR"),
            @ApiResponse(responseCode = "404", description = "Reembolso no encontrado"),
            @ApiResponse(responseCode = "409", description = "El reembolso no está en estado REQUESTED")
    })
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> approveRefund(
            @Parameter(description = "Id del reembolso", example = "1") @PathVariable Integer id) {
        RefundResponseDTO response = refundService.approveRefund(id);
        return responseBuilder.buildResponse("Refund approved and processed successfully", HttpStatus.OK, response);
    }

    // Rechaza un reembolso pendiente (solo administradores).
    @Operation(summary = "Rechazar reembolso", description = "Requiere rol ADMINISTRATOR. Solo reembolsos en estado REQUESTED pueden rechazarse; "
            + "no afecta asientos, reserva ni pago")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reembolso rechazado exitosamente",
                    content = @Content(schema = @Schema(implementation = RefundResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR"),
            @ApiResponse(responseCode = "404", description = "Reembolso no encontrado"),
            @ApiResponse(responseCode = "409", description = "El reembolso no está en estado REQUESTED")
    })
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> rejectRefund(
            @Parameter(description = "Id del reembolso", example = "1") @PathVariable Integer id) {
        RefundResponseDTO response = refundService.rejectRefund(id);
        return responseBuilder.buildResponse("Refund rejected successfully", HttpStatus.OK, response);
    }
}
