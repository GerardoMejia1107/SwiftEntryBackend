package com.gerardo.swiftentrybackend.domain.Ticket.service.validation.handlers;

import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationContext;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationHandler;
import org.springframework.stereotype.Component;

// Segundo paso: rechaza el escaneo si el ticket no está en estado ISSUED (ya usado,
// cancelado o reembolsado).
@Component
public class TicketStatusHandler extends TicketValidationHandler {

    @Override
    protected void process(TicketValidationContext context) {

        TicketStatus status = context.getTicket().getStatus();

        if (status != TicketStatus.ISSUED) {
            throw new ResourceConflictException(
                    "El ticket no puede ser validado por su estado: "
                            + status);
        }
    }
}
