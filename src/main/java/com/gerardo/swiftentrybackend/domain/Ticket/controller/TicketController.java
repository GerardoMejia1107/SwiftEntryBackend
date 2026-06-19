package com.gerardo.swiftentrybackend.domain.Ticket.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
}
