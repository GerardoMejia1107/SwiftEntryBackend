package com.gerardo.swiftentrybackend.domain.Report;

// Proyección de solo lectura con el conteo de asientos totales y disponibles por evento
public interface EventAvailabilityProjection {

    Integer getEventId();

    String getEventName();

    Long getTotalSeats();

    Long getAvailableSeats();
}
