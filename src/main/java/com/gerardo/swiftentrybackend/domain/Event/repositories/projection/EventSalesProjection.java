package com.gerardo.swiftentrybackend.domain.Event.repositories.projection;

import java.math.BigDecimal;

// Proyección usada por ReservationSeatRepository para agregar ventas (boletos vendidos e ingresos) por evento.
public interface EventSalesProjection {

    Integer getEventId();

    String getEventName();

    Long getTicketsSold();

    BigDecimal getRevenue();
}
