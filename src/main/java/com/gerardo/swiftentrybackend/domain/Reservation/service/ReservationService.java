package com.gerardo.swiftentrybackend.domain.Reservation.service;

import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationRequestDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.response.ReservationResponseDTO;

import java.util.List;

// Contrato para el ciclo de vida de una reserva: creación, consulta, cancelación y expiración
public interface ReservationService {

    // Aparta los asientos seleccionados por 15 minutos (máximo 5 asientos)
    ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO, String userEmail);

    List<ReservationResponseDTO> getAllReservations();

    ReservationResponseDTO getReservationById(Integer id);

    List<ReservationResponseDTO> getMyReservations(String userEmail);

    // Cancela la reserva y libera todos sus asientos
    ReservationResponseDTO cancelReservation(Integer id, String userEmail);

    // Quita un asiento de una reserva PENDING; si era el último, cancela la reserva completa
    ReservationResponseDTO removeSeatFromReservation(Integer reservationId, Integer reservationSeatId, String userEmail);

    List<ReservationResponseDTO> getReservationsByOrganizer(String organizerEmail);

    // Invocado por el scheduler: marca EXPIRED las reservas PENDING vencidas y libera sus asientos
    Integer expirePendingReservations();
}
