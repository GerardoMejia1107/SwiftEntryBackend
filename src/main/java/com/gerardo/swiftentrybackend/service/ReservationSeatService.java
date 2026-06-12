package com.gerardo.swiftentrybackend.service;

import com.gerardo.swiftentrybackend.dto.reservationseat.ReservationSeatResponseDTO;
import com.gerardo.swiftentrybackend.dto.reservationseat.ReservationSeatUpdateDTO;

import java.util.List;

public interface ReservationSeatService {

    List<ReservationSeatResponseDTO> getAllReservationSeats();

    ReservationSeatResponseDTO getReservationSeatById(Integer id);

    List<ReservationSeatResponseDTO> getReservationSeatsByReservationId(Integer reservationId);

    List<ReservationSeatResponseDTO> getReservationSeatsBySeatId(Integer seatId);

    ReservationSeatResponseDTO updateReservationSeat(
            Integer id,
            ReservationSeatUpdateDTO updateDTO
    );
}
