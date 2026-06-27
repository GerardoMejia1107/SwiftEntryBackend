package com.gerardo.swiftentrybackend.domain.Ticket.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketTransferRequestDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketValidateRequestDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketTransferResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/swift_entry/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final ResponseBuilder responseBuilder;

    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyTickets(Authentication authentication) {
        List<TicketResponseDTO> response = ticketService.getMyTickets(authentication.getName());
        return responseBuilder.buildResponse(
                "Tickets retrieved successfully", HttpStatus.OK, response);
    }

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
