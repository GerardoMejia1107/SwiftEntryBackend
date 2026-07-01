package com.gerardo.swiftentrybackend.domain.Report.services;

import com.gerardo.swiftentrybackend.domain.Report.EventAvailabilityProjection;
import com.gerardo.swiftentrybackend.domain.Report.dto.response.ReportResponseDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import com.gerardo.swiftentrybackend.domain.Reservation.repositories.ReservationSeatRepository;
import com.gerardo.swiftentrybackend.domain.Seat.repositories.LocalitySeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final LocalitySeatRepository localityRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    public List<ReportResponseDTO.EventAvailabilityReportDto> getSeatAvailabilityReport() {

        return localityRepository.getSeatAvailabilityReport()
                .stream()
                .map(r -> {
                    Integer totalSeats = r.getTotalSeats().intValue();
                    Integer availableSeats = r.getAvailableSeats().intValue();
                    Integer occupiedSeats = totalSeats - availableSeats;

                    double availabilityPercentage = totalSeats == 0
                            ? 0.0
                            : (availableSeats * 100.0) / totalSeats;

                    return new ReportResponseDTO.EventAvailabilityReportDto(
                            r.getEventId(),
                            r.getEventName(),
                            totalSeats,
                            availableSeats,
                            occupiedSeats,
                            availabilityPercentage
                    );
                })
                .toList();
    }



    public List<ReportResponseDTO.EventSalesReportDto> getSalesReport() {
        return reservationSeatRepository.getSalesReport(ReservationStatus.CONFIRMED)
                .stream()
                .map(r -> new ReportResponseDTO.EventSalesReportDto(
                        r.getEventId(),
                        r.getEventName(),
                        r.getTicketsSold(),
                        r.getRevenue()
                ))
                .toList();
    }
}
