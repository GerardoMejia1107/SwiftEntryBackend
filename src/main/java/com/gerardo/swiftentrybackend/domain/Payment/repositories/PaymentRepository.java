package com.gerardo.swiftentrybackend.domain.Payment.repositories;

import com.gerardo.swiftentrybackend.domain.Payment.PaymentModel;
import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentModel, Integer> {

    List<PaymentModel> findByReservationId(Integer reservationId);

    List<PaymentModel> findByStatus(PaymentStatus status);

    Optional<PaymentModel> findByTransactionReference(String transactionReference);

    boolean existsByTransactionReference(String transactionReference);

    boolean existsByReservationIdAndStatus(
            Integer reservationId,
            PaymentStatus status
    );

    List<PaymentModel> findByReservation_User_Email(String email);
}
