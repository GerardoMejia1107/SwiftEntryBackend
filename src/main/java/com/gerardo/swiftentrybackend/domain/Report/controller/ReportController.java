package com.gerardo.swiftentrybackend.domain.Report.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Report.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swift_entry/reports")
@RequiredArgsConstructor
// Expone reportes administrativos agregados sobre eventos (disponibilidad de asientos y ventas)
public class ReportController {

    private final ReportService reportService;
    private final ResponseBuilder responseBuilder;

    // Reporte de ocupación: asientos totales, disponibles y ocupados por evento
    @GetMapping("/events/seat-availability")
    public ResponseEntity<GeneralResponse> getSeatAvailability() {
        return responseBuilder.buildResponse(
                "Seat availability report retrieved successfully",
                HttpStatus.OK,
                reportService.getSeatAvailabilityReport()
        );
    }

    // Reporte de ventas: boletos vendidos e ingresos por evento, sobre reservas confirmadas
    @GetMapping("/events/sales")
    public ResponseEntity<GeneralResponse> getSalesReport() {
        return responseBuilder.buildResponse(
                "Sales report retrieved successfully",
                HttpStatus.OK,
                reportService.getSalesReport()
        );
    }
}
