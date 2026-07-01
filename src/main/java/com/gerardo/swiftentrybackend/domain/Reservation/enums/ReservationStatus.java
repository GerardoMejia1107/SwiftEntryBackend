package com.gerardo.swiftentrybackend.domain.Reservation.enums;

// Estados del ciclo de vida de una reserva
public enum ReservationStatus {
    PENDING,    // asientos apartados, esperando pago (expira a los 15 min)
    CONFIRMED,  // pago aprobado; asientos SOLD y tickets emitidos
    EXPIRED,    // no se pagó a tiempo; asientos liberados por el scheduler o al reintentar el pago
    CANCELLED,  // cancelada por el usuario antes de pagar
    REFUNDED
}
