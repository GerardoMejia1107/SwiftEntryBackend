package com.gerardo.swiftentrybackend.domain.Ticket.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketTransferRequestDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketValidateRequestDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketTransferResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Mis boletos", description = "Lista los boletos activos del usuario autenticado")
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyTickets(Authentication authentication) {
        List<TicketResponseDTO> response = ticketService.getMyTickets(authentication.getName());
        return responseBuilder.buildResponse(
                "Tickets retrieved successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Validar boleto por QR", description = "Escaneo en puerta: verifica que el QR sea válido, marca el boleto como USED y registra quién lo validó")
    @PostMapping("/validate")
    public ResponseEntity<GeneralResponse> validateTicket(
            @Valid @RequestBody TicketValidateRequestDTO request,
            Authentication authentication
    ) {
        TicketResponseDTO response = ticketService.validateTicketByQrCode(
                request.getQrCode(), authentication.getName());
        return responseBuilder.buildResponse(
                "Ticket validated successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Imagen QR del boleto", description = "Retorna la imagen PNG del código QR. Solo el titular puede solicitarla")
    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> getTicketQr(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        byte[] image = ticketService.getTicketQrImage(id, authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    @Operation(summary = "Transferir boleto", description = "Cede el boleto a otro usuario por email. Queda registrado en ticket_transfer")
    @PostMapping("/{ticketId}/transfer")
    public ResponseEntity<GeneralResponse> transferTicket(
            @PathVariable Integer ticketId,
            @Valid @RequestBody TicketTransferRequestDTO request,
            Authentication authentication
    ) {
        TicketTransferResponseDTO response = ticketService.transferTicket(
                ticketId, request.getReceiverEmail(), authentication.getName());
        return responseBuilder.buildResponse(
                "Ticket transferred successfully", HttpStatus.OK, response);
    }
}
