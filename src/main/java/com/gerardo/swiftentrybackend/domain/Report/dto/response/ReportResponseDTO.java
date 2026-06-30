package com.gerardo.swiftentrybackend.domain.Report.dto.response;

public class ReportResponseDTO {

    public record EventAvailabilityReportDto(
            Integer eventId,
            String eventName,
            Integer totalSeats,
            Integer availableSeats,
            Integer reservedSeats,
            Double availabilityPercentage
    ) {}
}
