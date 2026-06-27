package com.gerardo.swiftentrybackend.domain.Ticket.service.validation.handlers;

import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.Ticket.repositories.TicketRepository;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationContext;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketExistsHandler extends TicketValidationHandler {

    private final TicketRepository ticketRepository;

    @Override
    protected void process(TicketValidationContext context) {

        TicketModel ticket = ticketRepository
                .findByQrCode(context.getQrCode())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ticket not found with QR code: "
                                        + context.getQrCode()));

        context.setTicket(ticket);
    }
}
