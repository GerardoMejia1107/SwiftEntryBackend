package com.gerardo.swiftentrybackend.domain.Ticket.service.validation;

import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketValidationContext {

    private String qrCode;

    private String validatorEmail;

    private TicketModel ticket;

    private UserModel validator;
}
