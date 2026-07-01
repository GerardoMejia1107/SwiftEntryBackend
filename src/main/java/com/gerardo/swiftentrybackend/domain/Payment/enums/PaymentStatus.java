package com.gerardo.swiftentrybackend.domain.Payment.enums;

// Estados del ciclo de vida de un pago
public enum PaymentStatus {
    PENDING,    // creado pero aún no resuelto
    APPROVED,   // pago exitoso: reserva pasa a CONFIRMED y se emiten los tickets
    FAILED,     // intento rechazado; la reserva queda PENDING para reintentar
    CANCELLED,
    REFUNDED
}
