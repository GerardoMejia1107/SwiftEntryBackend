package com.gerardo.swiftentrybackend.domain.Report.controller;

import com.gerardo.swiftentrybackend.domain.Report.dto.response.ReportResponseDTO;
import com.gerardo.swiftentrybackend.domain.Report.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/events/seat-availability")
    public ResponseEntity<List<ReportResponseDTO.EventAvailabilityReportDto>> getSeatAvailability() {
        return ResponseEntity.ok(reportService.getSeatAvailabilityReport());
    }

    @GetMapping("/events/sales")
    public ResponseEntity<List<ReportResponseDTO.EventSalesReportDto>> getSalesReport() {
        return ResponseEntity.ok(reportService.getSalesReport());
    }
}
