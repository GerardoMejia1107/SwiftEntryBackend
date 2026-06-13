package com.gerardo.swiftentrybackend.domain.Seat.services;

import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface SeatService {
    List<SeatResponseDTO >createSeats(SeatRequestDTO request);
    List<SeatResponseDTO> getAllSeats();
    SeatResponseDTO getSeatById(Long id);
    List<SeatResponseDTO> getSeatsByLocalityId(Long localityId);
    //SeatResponseDTO updateSeat(Long id, SeatUpdateDTO request);
    void deleteSeat(Long id);
}
