package com.gerardo.swiftentrybackend.domain.Event.repositories.projection;

import java.math.BigDecimal;

public interface EventSalesProjection {

    Integer getEventId();

    String getEventName();

    Long getTicketsSold();

    BigDecimal getRevenue();
}
