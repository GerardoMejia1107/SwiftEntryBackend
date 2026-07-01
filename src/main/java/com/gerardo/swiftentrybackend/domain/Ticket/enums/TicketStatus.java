package com.gerardo.swiftentrybackend.domain.Ticket.enums;

// Estados del ciclo de vida de un ticket emitido tras el pago de una reserva.
public enum TicketStatus {
    ISSUED,   // Emitido y válido para acceso, aún no escaneado
    USED,     // Ya validado/escaneado en el ingreso al evento
    CANCELLED,
    REFUNDED
}
