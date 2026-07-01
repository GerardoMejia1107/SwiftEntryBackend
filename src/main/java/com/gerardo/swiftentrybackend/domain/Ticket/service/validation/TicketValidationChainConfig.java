package com.gerardo.swiftentrybackend.domain.Ticket.service.validation;

import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.handlers.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Ensambla la cadena de responsabilidad de validación de tickets (orden fijo de handlers)
// y la expone como bean para que TicketServiceImpl.validateTicketByQrCode la use.
@Configuration
@RequiredArgsConstructor
public class TicketValidationChainConfig {

    private final TicketExistsHandler ticketExistsHandler;
    private final TicketStatusHandler ticketStatusHandler;
    private final ValidatorExistsHandler validatorExistsHandler;
    private final EventOwnershipHandler eventOwnershipHandler;
    private final EventDateHandler eventDateHandler;

    // Orden: existe el ticket -> estado válido -> existe el validador -> el validador
    // pertenece al evento -> la fecha del evento permite el ingreso.
    @Bean
    public TicketValidationHandler ticketValidationChain() {

        ticketExistsHandler
                .setNext(ticketStatusHandler)
                .setNext(validatorExistsHandler)
                .setNext(eventOwnershipHandler)
                .setNext(eventDateHandler);

        return ticketExistsHandler;
    }
}
