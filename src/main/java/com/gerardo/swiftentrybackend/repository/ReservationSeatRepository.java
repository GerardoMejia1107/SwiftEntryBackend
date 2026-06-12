package com.gerardo.swiftentrybackend.repository;

import com.gerardo.swiftentrybackend.domain.Reservation.ReservationSeatModel;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeatModel, Integer> {

    List<ReservationSeatModel> findByReservationId(Integer reservationId);

    List<ReservationSeatModel> findBySeatId(Integer seatId);

    boolean existsBySeatIdAndReservationStatus(
            Integer seatId,
            ReservationStatus status
    );

    Optional<ReservationSeatModel> findByReservationIdAndSeatId(
            Integer reservationId,
            Integer seatId
    );
}
