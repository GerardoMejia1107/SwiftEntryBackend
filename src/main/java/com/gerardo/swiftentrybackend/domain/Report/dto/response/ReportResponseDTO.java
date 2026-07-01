package com.gerardo.swiftentrybackend.domain.Report.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ReportResponseDTO {

    public record EventAvailabilityReportDto(
            Integer eventId,
            String eventName,
            Integer totalSeats,
            Integer availableSeats,
            Integer occupiedSeats,
            Double availabilityPercentage
    ) {}

    public record EventSalesReportDto(
            Integer eventId,
            String eventName,
            Long ticketsSold,
            BigDecimal revenue
    ) {}


}
