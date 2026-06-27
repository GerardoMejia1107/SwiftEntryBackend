package com.gerardo.swiftentrybackend.domain.Ticket.service.validation.handlers;

import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationContext;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationHandler;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import org.springframework.stereotype.Component;

@Component
public class EventOwnershipHandler
        extends TicketValidationHandler {

    @Override
    protected void process(
            TicketValidationContext context) {

        TicketModel ticket = context.getTicket();

        UserModel validator = context.getValidator();

        String organizerEmail =
                ticket.getReservation()
                        .getReservationSeats()
                        .stream()
                        .findFirst()
                        .orElseThrow()
                        .getLocalitySeat()
                        .getLocality()
                        .getEvent()
                        .getOrganizer()
                        .getEmail();

        if (!organizerEmail.equalsIgnoreCase(
                validator.getEmail())) {

            throw new ForbiddenOperationException(
                    "No está autorizado para realizar esta acción.");
        }
    }
}
