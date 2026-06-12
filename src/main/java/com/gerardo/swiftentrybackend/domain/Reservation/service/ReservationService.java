package com.gerardo.swiftentrybackend.domain.Reservation.service;

import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.response.ReservationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationUpdateDTO;

import java.util.List;

public interface ReservationService {
// TODO: fix ReservationRequestDTO
//    ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO);

    List<ReservationResponseDTO> getAllReservations();

    ReservationResponseDTO getReservationById(Integer id);

    List<ReservationResponseDTO> getReservationsByUserId(Integer userId);

    List<ReservationResponseDTO> getReservationsByStatus(ReservationStatus status);

    ReservationResponseDTO updateReservation(Integer id, ReservationUpdateDTO updateDTO);

    ReservationResponseDTO cancelReservation(Integer id);

    ReservationResponseDTO expireReservation(Integer id);

    Integer expirePendingReservations();
}
