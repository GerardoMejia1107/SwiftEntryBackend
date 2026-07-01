package com.gerardo.swiftentrybackend.domain.Ticket.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketTransferRequestDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketValidateRequestDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketTransferResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.service.TicketService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Boletos", description = "Consulta, validación QR y transferencia de boletos")
@RestController
@RequestMapping("/swift_entry/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final ResponseBuilder responseBuilder;

    @Operation(summary = "Mis boletos", description = "Lista los boletos vigentes del usuario autenticado (titular actual, considerando transferencias)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Boletos obtenidos exitosamente",
                    content = @Content(schema = @Schema(implementation = TicketResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyTickets(Authentication authentication) {
        List<TicketResponseDTO> response = ticketService.getMyTickets(authentication.getName());
        return responseBuilder.buildResponse(
                "Tickets retrieved successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Validar boleto por QR",
            description = "Escaneo en puerta: verifica que el QR sea válido, marca el boleto como USED y registra quién lo validó. "
                    + "Internamente ejecuta una cadena de responsabilidad de varios pasos (existencia del boleto, existencia del validador, "
                    + "pertenencia del evento, fecha del evento, estado del boleto) antes de aceptar la validación")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Boleto validado exitosamente",
                    content = @Content(schema = @Schema(implementation = TicketResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Código QR no proporcionado o inválido (validación de datos)"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el organizador del evento del boleto"),
            @ApiResponse(responseCode = "404", description = "Boleto no encontrado con ese código QR, o validador no encontrado"),
            @ApiResponse(responseCode = "409", description = "El evento aún no inicia / ya finalizó, o el boleto no está en estado ISSUED")
    })
    @PostMapping("/validate")
    public ResponseEntity<GeneralResponse> validateTicket(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Código QR leído en el punto de acceso", required = true)
            TicketValidateRequestDTO request,
            Authentication authentication
    ) {
        TicketResponseDTO response = ticketService.validateTicketByQrCode(
                request.getQrCode(), authentication.getName());
        return responseBuilder.buildResponse(
                "Ticket validated successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Imagen QR del boleto", description = "Retorna la imagen PNG del código QR. Solo el titular actual puede solicitarla. "
            + "A diferencia del resto de endpoints, esta respuesta NO usa el envoltorio GeneralResponse: retorna los bytes crudos de la imagen")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen PNG del código QR generada exitosamente",
                    content = @Content(mediaType = "image/png")),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el titular actual del boleto"),
            @ApiResponse(responseCode = "404", description = "Boleto no encontrado")
    })
    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> getTicketQr(
            @Parameter(description = "Id del boleto", example = "1") @PathVariable Integer id,
            Authentication authentication
    ) {
        byte[] image = ticketService.getTicketQrImage(id, authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    @Operation(summary = "Transferir boleto", description = "Cede el boleto a otro usuario registrado, identificado por su email. "
            + "Solo el titular actual puede transferir y solo boletos en estado ISSUED; rota el código de ticket y el QR, "
            + "y queda registrado en ticket_transfer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Boleto transferido exitosamente",
                    content = @Content(schema = @Schema(implementation = TicketTransferResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Email del receptor no proporcionado o inválido (validación de datos)"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el titular actual del boleto"),
            @ApiResponse(responseCode = "404", description = "Boleto no encontrado, o no existe una cuenta con el email del receptor"),
            @ApiResponse(responseCode = "409", description = "El boleto no está en estado ISSUED, o se intenta transferir a uno mismo")
    })
    @PostMapping("/{ticketId}/transfer")
    public ResponseEntity<GeneralResponse> transferTicket(
            @Parameter(description = "Id del boleto a transferir", example = "1") @PathVariable Integer ticketId,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Email del usuario que recibirá el boleto", required = true)
            TicketTransferRequestDTO request,
            Authentication authentication
    ) {
        TicketTransferResponseDTO response = ticketService.transferTicket(
                ticketId, request.getReceiverEmail(), authentication.getName());
        return responseBuilder.buildResponse(
                "Ticket transferred successfully", HttpStatus.OK, response);
    }
}
