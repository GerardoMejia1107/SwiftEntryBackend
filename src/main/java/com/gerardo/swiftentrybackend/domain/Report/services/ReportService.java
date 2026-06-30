package com.gerardo.swiftentrybackend.domain.Report.services;

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
                .map(row -> {

                    Integer eventId = (Integer) row[0];
                    String eventName = (String) row[1];
                    Integer totalSeats = ((Long) row[2]).intValue();
                    Integer availableSeats = ((Long) row[3]).intValue();

                    Integer reservedSeats = totalSeats - availableSeats;

                    double percentage = totalSeats == 0
                            ? 0.0
                            : (availableSeats * 100.0) / totalSeats;

                    return new ReportResponseDTO.EventAvailabilityReportDto(
                            eventId,
                            eventName,
                            totalSeats,
                            availableSeats,
                            reservedSeats,
                            percentage
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
