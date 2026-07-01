package com.gerardo.swiftentrybackend.domain.Ticket.service.validation;

import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import lombok.Getter;
import lombok.Setter;

// Estado mutable que se pasa a lo largo de la cadena de validación: cada handler lee y
// completa datos (ticket, validador) según avanza el proceso.
@Getter
@Setter
public class TicketValidationContext {

    private String qrCode;

    private String validatorEmail;

    private TicketModel ticket;

    private UserModel validator;
}
