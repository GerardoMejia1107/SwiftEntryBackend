package com.gerardo.swiftentrybackend.domain.Reservation.repositories;

import com.gerardo.swiftentrybackend.domain.Reservation.ReservationSeatModel;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeatModel, Integer> {

    List<ReservationSeatModel> findByReservation_Id(Integer reservationId);

    List<ReservationSeatModel> findByLocalitySeat_Id(Long localitySeatId);

    boolean existsByLocalitySeat_Id(Long localitySeatId);

    boolean existsByLocalitySeat_IdAndReservation_Status(Long localitySeatId, ReservationStatus status);

    boolean existsByLocalitySeat_Locality_Event_Id(Integer eventId);

    boolean existsByLocalitySeat_Locality_Id(Long localityId);

    boolean existsByLocalitySeat_Seat_Id(Long seatId);

    Optional<ReservationSeatModel> findByReservation_IdAndLocalitySeat_Id(Integer reservationId, Long localitySeatId);
}
