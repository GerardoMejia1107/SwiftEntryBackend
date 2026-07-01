package com.gerardo.swiftentrybackend.domain.Report.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder
// Contenedor de los DTOs devueltos por los endpoints de reportes
public class ReportResponseDTO {

    // Disponibilidad de asientos de un evento (totales, disponibles, ocupados y porcentaje)
    public record EventAvailabilityReportDto(
            Integer eventId,
            String eventName,
            Integer totalSeats,
            Integer availableSeats,
            Integer occupiedSeats,
            Double availabilityPercentage
    ) {}

    // Ventas de un evento (boletos vendidos e ingresos totales)
    public record EventSalesReportDto(
            Integer eventId,
            String eventName,
            Long ticketsSold,
            BigDecimal revenue
    ) {}


}
