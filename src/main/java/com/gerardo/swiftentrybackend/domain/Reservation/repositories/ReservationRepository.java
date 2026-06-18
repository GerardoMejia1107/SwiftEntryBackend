package com.gerardo.swiftentrybackend.domain.Reservation.repositories;


import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationModel, Integer> {

    List<ReservationModel> findByUser_Id(Integer userId);

    List<ReservationModel> findByStatus(ReservationStatus status);

    Optional<ReservationModel> findByIdAndUser_Id(Integer id, Integer userId);

    boolean existsByUser_IdAndStatus(Integer userId, ReservationStatus status);

    // Fetches expired PENDING reservations with all associations loaded to avoid N+1
    @Query("SELECT DISTINCT r FROM ReservationModel r " +
           "JOIN FETCH r.reservationSeats rs " +
           "JOIN FETCH rs.localitySeat ls " +
           "JOIN FETCH ls.locality " +
           "WHERE r.status = :status AND r.expiresAt < :now")
    List<ReservationModel> findExpiredReservations(
            @Param("status") ReservationStatus status,
            @Param("now") LocalDateTime now
    );

    // Fetches all reservations for events where the given user is the organizer
    @Query("SELECT DISTINCT r FROM ReservationModel r " +
           "JOIN r.reservationSeats rs " +
           "JOIN rs.localitySeat ls " +
           "JOIN ls.locality l " +
           "JOIN l.event e " +
           "WHERE e.organizer.id = :organizerId")
    List<ReservationModel> findByOrganizerOfEvent(@Param("organizerId") Integer organizerId);
}
