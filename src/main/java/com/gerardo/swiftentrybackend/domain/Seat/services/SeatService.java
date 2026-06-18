package com.gerardo.swiftentrybackend.domain.Seat.services;

import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatAssignmentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.LocalitySeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatMapResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;

import java.util.List;

public interface SeatService {
    void initializeSeats();
    List<LocalitySeatResponseDTO> assignSeats(SeatAssignmentRequestDTO request);
    List<SeatMapResponseDTO> getSeatMapByEventId(Integer eventId);
    List<SeatResponseDTO> getAllSeats();
    SeatResponseDTO getSeatById(Long id);
    List<LocalitySeatResponseDTO> getSeatsByLocalityId(Long localityId);
    void deleteSeat(Long id);
}
