package com.gerardo.swiftentrybackend.domain.Report.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Report.dto.response.ReportResponseDTO;
import com.gerardo.swiftentrybackend.domain.Report.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reportes", description = "Reportes administrativos agregados sobre eventos (disponibilidad de asientos y ventas). Requieren rol ADMINISTRATOR")
@RestController
@RequestMapping("/swift_entry/reports")
@RequiredArgsConstructor
// Expone reportes administrativos agregados sobre eventos (disponibilidad de asientos y ventas)
public class ReportController {

    private final ReportService reportService;
    private final ResponseBuilder responseBuilder;

    // Reporte de ocupación: asientos totales, disponibles y ocupados por evento
    @Operation(summary = "Reporte de disponibilidad de asientos",
            description = "Requiere rol ADMINISTRATOR. Devuelve, por cada evento, el total de asientos, los disponibles, los ocupados y el porcentaje de disponibilidad, calculado a partir de los LocalitySeat de todas las localidades del evento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte de disponibilidad obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = ReportResponseDTO.EventAvailabilityReportDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR")
    })
    @GetMapping("/events/seat-availability")
    public ResponseEntity<GeneralResponse> getSeatAvailability() {
        return responseBuilder.buildResponse(
                "Seat availability report retrieved successfully",
                HttpStatus.OK,
                reportService.getSeatAvailabilityReport()
        );
    }

    // Reporte de ventas: boletos vendidos e ingresos por evento, sobre reservas confirmadas
    @Operation(summary = "Reporte de ventas",
            description = "Requiere rol ADMINISTRATOR. Devuelve, por cada evento, la cantidad de boletos vendidos y los ingresos totales, calculados sobre los asientos de reservas en estado CONFIRMED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte de ventas obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = ReportResponseDTO.EventSalesReportDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR")
    })
    @GetMapping("/events/sales")
    public ResponseEntity<GeneralResponse> getSalesReport() {
        return responseBuilder.buildResponse(
                "Sales report retrieved successfully",
                HttpStatus.OK,
                reportService.getSalesReport()
        );
    }
}
