package com.gerardo.swiftentrybackend.domain.Seat.enums;

// Estado de un LocalitySeat; es la fuente de verdad de disponibilidad del asiento dentro de una localidad.
public enum SeatStatus {
    // Libre para ser reservado.
    AVAILABLE,
    // Retenido temporalmente por un usuario mientras dura su reserva (con expiración).
    RESERVED,
    // Comprado; ya tiene ticket emitido.
    SOLD,
    // Bloqueado manualmente, no disponible para la venta.
    BLOCKED,
    // Deshabilitado permanentemente (no forma parte del inventario vendible).
    DISABLED
}