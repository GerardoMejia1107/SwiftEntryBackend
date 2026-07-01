package com.gerardo.swiftentrybackend.domain.Refund.repositories;

import com.gerardo.swiftentrybackend.domain.Refund.RefundModel;
import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundRepository extends JpaRepository<RefundModel, Integer> {

    List<RefundModel> findByPaymentId(Integer paymentId);

    List<RefundModel> findByStatus(RefundStatus status);

    // Verifica si ya existe un reembolso de un pago en un estado dado (evita duplicados).
    boolean existsByPaymentIdAndStatus(
            Integer paymentId,
            RefundStatus status
    );
}