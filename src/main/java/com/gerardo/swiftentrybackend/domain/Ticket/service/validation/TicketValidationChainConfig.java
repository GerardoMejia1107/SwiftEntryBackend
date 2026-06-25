package com.gerardo.swiftentrybackend.domain.Ticket.service.validation;

import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.handlers.TicketExistsHandler;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.handlers.TicketStatusHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TicketValidationChainConfig {

    private final TicketExistsHandler ticketExistsHandler;
    private final TicketStatusHandler ticketStatusHandler;
//    TODO: validaciones de evento
//    private final EventOwnershipHandler eventOwnershipHandler;
//    private final EventDateHandler eventDateHandler;

    @Bean
    public TicketValidationHandler ticketValidationChain() {

        ticketExistsHandler
                .setNext(ticketStatusHandler);
        //    TODO: validaciones de evento
//                .setNext(eventOwnershipHandler)
//                .setNext(eventDateHandler);

        return ticketExistsHandler;
    }
}
