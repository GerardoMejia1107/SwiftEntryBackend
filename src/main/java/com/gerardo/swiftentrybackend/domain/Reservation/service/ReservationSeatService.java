package com.gerardo.swiftentrybackend.domain.Reservation.service;

import com.gerardo.swiftentrybackend.domain.Reservation.dto.response.ReservationSeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationSeatUpdateDTO;

import java.util.List;

// Contrato para consultar y actualizar asientos individuales de una reserva; sin implementación aún (WIP)
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
