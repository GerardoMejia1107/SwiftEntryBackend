package com.gerardo.swiftentrybackend.domain.Ticket.service.validation;

public abstract class TicketValidationHandler {

    private TicketValidationHandler next;

    public TicketValidationHandler setNext(
            TicketValidationHandler next) {

        this.next = next;
        return next;
    }

    public void handle(
            TicketValidationContext context) {

        process(context);

        if (next != null) {
            next.handle(context);
        }
    }

    protected abstract void process(
            TicketValidationContext context);
}
