package com.gerardo.swiftentrybackend.domain.Report;

public interface EventAvailabilityProjection {

    Integer getEventId();

    String getEventName();

    Long getTotalSeats();

    Long getAvailableSeats();
}
