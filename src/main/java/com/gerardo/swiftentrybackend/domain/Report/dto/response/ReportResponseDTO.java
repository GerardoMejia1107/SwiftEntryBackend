package com.gerardo.swiftentrybackend.domain.Report.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Schema(description = "Contenedor de los DTOs devueltos por los endpoints de reportes")
// Contenedor de los DTOs devueltos por los endpoints de reportes
public class ReportResponseDTO {

    // Disponibilidad de asientos de un evento (totales, disponibles, ocupados y porcentaje)
    @Schema(description = "Disponibilidad de asientos de un evento: totales, disponibles, ocupados y porcentaje de disponibilidad")
    public record EventAvailabilityReportDto(
            @Schema(description = "ID del evento", example = "1") Integer eventId,
            @Schema(description = "Nombre del evento", example = "Concierto de Rock") String eventName,
            @Schema(description = "Cantidad total de asientos del evento, sumando todas sus localidades", example = "500") Integer totalSeats,
            @Schema(description = "Cantidad de asientos aún disponibles (status AVAILABLE)", example = "320") Integer availableSeats,
            @Schema(description = "Cantidad de asientos ocupados (reservados o vendidos)", example = "180") Integer occupiedSeats,
            @Schema(description = "Porcentaje de asientos disponibles sobre el total", example = "64.0") Double availabilityPercentage
    ) {}

    // Ventas de un evento (boletos vendidos e ingresos totales)
    @Schema(description = "Ventas de un evento: boletos vendidos e ingresos totales, calculados sobre reservas CONFIRMED")
    public record EventSalesReportDto(
            @Schema(description = "ID del evento", example = "1") Integer eventId,
            @Schema(description = "Nombre del evento", example = "Concierto de Rock") String eventName,
            @Schema(description = "Cantidad de boletos vendidos (asientos de reservas CONFIRMED)", example = "180") Long ticketsSold,
            @Schema(description = "Ingresos totales generados por el evento", example = "5400.00") BigDecimal revenue
    ) {}


}
