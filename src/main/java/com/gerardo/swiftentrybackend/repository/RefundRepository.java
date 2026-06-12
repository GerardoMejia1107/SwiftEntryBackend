package com.gerardo.swiftentrybackend.repository;

import com.gerardo.swiftentrybackend.domain.Refund.RefundModel;
import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundRepository extends JpaRepository<RefundModel, Integer> {

    List<RefundModel> findByPaymentId(Integer paymentId);

    List<RefundModel> findByStatus(RefundStatus status);

    boolean existsByPaymentIdAndStatus(
            Integer paymentId,
            RefundStatus status
    );
}