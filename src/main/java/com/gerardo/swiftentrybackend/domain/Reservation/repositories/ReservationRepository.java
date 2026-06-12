package com.gerardo.swiftentrybackend.domain.Reservation.repositories;


import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationModel, Integer> {

    List<ReservationModel> findByUserId(Integer userId);

    List<ReservationModel> findByStatus(ReservationStatus status);

    List<ReservationModel> findByStatusAndExpiresAtBefore(
            ReservationStatus status,
            LocalDateTime expiresAt
    );

    Optional<ReservationModel> findByIdAndUserId(Integer id, Integer userId);

    boolean existsByUserIdAndStatus(Integer userId, ReservationStatus status);
}
