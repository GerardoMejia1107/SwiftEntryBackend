package com.gerardo.swiftentrybackend.domain.Payment.repositories;

import com.gerardo.swiftentrybackend.domain.Payment.PaymentModel;
import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentModel, Integer> {

    // Pagos asociados a una reserva (puede haber más de uno si hubo intentos fallidos)
    List<PaymentModel> findByReservationId(Integer reservationId);

    List<PaymentModel> findByStatus(PaymentStatus status);

    Optional<PaymentModel> findByTransactionReference(String transactionReference);

    boolean existsByTransactionReference(String transactionReference);

    boolean existsByReservationIdAndStatus(
            Integer reservationId,
            PaymentStatus status
    );

    // Historial de pagos del usuario autenticado, vía la reserva
    List<PaymentModel> findByReservation_User_Email(String email);

    // Carga el pago junto con toda la cadena de relaciones necesaria para construir la respuesta completa
    @Query("SELECT p FROM PaymentModel p " +
           "JOIN FETCH p.reservation r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.reservationSeats rs " +
           "JOIN FETCH rs.localitySeat ls " +
           "JOIN FETCH ls.locality l " +
           "JOIN FETCH l.event " +
           "WHERE p.id = :id")
    Optional<PaymentModel> findByIdWithFullChain(@Param("id") Integer id);
}
