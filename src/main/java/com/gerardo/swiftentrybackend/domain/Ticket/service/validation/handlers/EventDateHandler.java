package com.gerardo.swiftentrybackend.domain.Ticket.service.validation.handlers;

import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationContext;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventDateHandler
        extends TicketValidationHandler {

    @Override
    protected void process(
            TicketValidationContext context) {

        TicketModel ticket = context.getTicket();

        var event =
                ticket.getReservation()
                        .getReservationSeats()
                        .stream()
                        .findFirst()
                        .orElseThrow()
                        .getLocalitySeat()
                        .getLocality()
                        .getEvent();

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(event.getStartDate())) {
            throw new ResourceConflictException(
                    "El evento no ha empezado");
        }

        if (now.isAfter(event.getEndDate())) {
            throw new ResourceConflictException(
                    "El evento terminado");
        }
    }
}
