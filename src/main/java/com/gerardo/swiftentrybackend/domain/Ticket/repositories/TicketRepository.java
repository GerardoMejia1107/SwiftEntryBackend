package com.gerardo.swiftentrybackend.domain.Ticket.repositories;

import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketModel, Integer> {

    List<TicketModel> findByReservationId(Integer reservationId);

    List<TicketModel> findBySeatId(Integer seatId);

    List<TicketModel> findByStatus(TicketStatus status);

    Optional<TicketModel> findByTicketCode(String ticketCode);

    Optional<TicketModel> findByQrCode(String qrCode);

    boolean existsByTicketCode(String ticketCode);

    boolean existsByQrCode(String qrCode);

    boolean existsBySeatIdAndStatus(Integer seatId, TicketStatus status);

    List<TicketModel> findByReservation_User_Email(String email);

    // Devuelve los tickets "en poder" del usuario: si fueron transferidos, el poseedor
    // actual; si no, el comprador original de la reserva.
    @Query("SELECT t FROM TicketModel t " +
           "LEFT JOIN t.currentHolder ch " +
           "WHERE (ch IS NOT NULL AND ch.email = :email) " +
           "   OR (ch IS NULL AND t.reservation.user.email = :email)")
    List<TicketModel> findCurrentHolderTickets(@Param("email") String email);
}
