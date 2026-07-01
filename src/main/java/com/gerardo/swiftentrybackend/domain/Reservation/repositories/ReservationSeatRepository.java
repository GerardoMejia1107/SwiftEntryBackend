package com.gerardo.swiftentrybackend.domain.Reservation.repositories;

import com.gerardo.swiftentrybackend.domain.Event.repositories.projection.EventSalesProjection;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationSeatModel;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// Consultas sobre asientos individuales dentro de una reserva
public interface ReservationSeatRepository extends JpaRepository<ReservationSeatModel, Integer> {

    // Cuenta asientos que el usuario tiene reservados/comprados en un evento, en los estados dados
    @Query("SELECT COUNT(rs) FROM ReservationSeatModel rs " +
           "WHERE rs.reservation.user.id = :userId " +
           "AND rs.localitySeat.locality.event.id = :eventId " +
           "AND rs.reservation.status IN :statuses")
    long countByUserAndEventAndStatuses(
            @Param("userId") Integer userId,
            @Param("eventId") Integer eventId,
            @Param("statuses") List<ReservationStatus> statuses);

    List<ReservationSeatModel> findByReservation_Id(Integer reservationId);

    List<ReservationSeatModel> findByLocalitySeat_Id(Long localitySeatId);

    boolean existsByLocalitySeat_Id(Long localitySeatId);

    boolean existsByLocalitySeat_IdAndReservation_Status(Long localitySeatId, ReservationStatus status);

    boolean existsByLocalitySeat_Locality_Event_Id(Integer eventId);

    boolean existsByLocalitySeat_Locality_Id(Long localityId);

    boolean existsByLocalitySeat_Seat_Id(Long seatId);

    Optional<ReservationSeatModel> findByReservation_IdAndLocalitySeat_Id(Integer reservationId, Long localitySeatId);

    Optional<ReservationSeatModel> findByReservation_IdAndLocalitySeat_Seat_Id(Integer reservationId, Long seatId);

    // Reporte de ventas por evento: boletos vendidos e ingresos, para reservas en el estado dado
    @Query("""
        SELECT
            rs.localitySeat.locality.event.id AS eventId,
            rs.localitySeat.locality.event.name AS eventName,
            COUNT(rs.id) AS ticketsSold,
            COALESCE(SUM(rs.priceAtReservation), 0) AS revenue
        FROM ReservationSeatModel rs
        WHERE rs.reservation.status = :status
        GROUP BY
            rs.localitySeat.locality.event.id,
            rs.localitySeat.locality.event.name
    """)
    List<EventSalesProjection> getSalesReport(
            @Param("status") ReservationStatus status);
}
