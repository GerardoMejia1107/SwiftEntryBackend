package com.gerardo.swiftentrybackend.domain.Ticket.service.validation;

import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketValidationContext {

    private String ticketCode;

    private String organizerEmail;

    private TicketModel ticket;

}
