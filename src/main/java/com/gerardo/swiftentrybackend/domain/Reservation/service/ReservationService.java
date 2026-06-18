package com.gerardo.swiftentrybackend.domain.Reservation.service;

import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationRequestDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.response.ReservationResponseDTO;

import java.util.List;

public interface ReservationService {

    ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO, String userEmail);

    List<ReservationResponseDTO> getAllReservations();

    ReservationResponseDTO getReservationById(Integer id);

    List<ReservationResponseDTO> getMyReservations(String userEmail);

    ReservationResponseDTO cancelReservation(Integer id, String userEmail);

    List<ReservationResponseDTO> getReservationsByOrganizer(String organizerEmail);

    Integer expirePendingReservations();
}
