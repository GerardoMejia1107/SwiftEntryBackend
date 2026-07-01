package com.gerardo.swiftentrybackend.domain.Ticket.service.validation;

// Clase base del patrón Chain of Responsibility para la validación de tickets: cada
// subclase implementa un único chequeo y delega al siguiente handler si pasa.
public abstract class TicketValidationHandler {

    private TicketValidationHandler next;

    // Enlaza el siguiente handler de la cadena y lo retorna para permitir encadenar llamadas.
    public TicketValidationHandler setNext(
            TicketValidationHandler next) {

        this.next = next;
        return next;
    }

    // Ejecuta el chequeo propio y, si hay un siguiente handler, continúa la cadena.
    public void handle(
            TicketValidationContext context) {

        process(context);

        if (next != null) {
            next.handle(context);
        }
    }

    // Chequeo específico de cada handler; lanza excepción si la validación falla.
    protected abstract void process(
            TicketValidationContext context);
}
