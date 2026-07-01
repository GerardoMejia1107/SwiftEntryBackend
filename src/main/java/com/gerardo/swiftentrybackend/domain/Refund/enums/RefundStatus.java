package com.gerardo.swiftentrybackend.domain.Refund.enums;

// Estados del ciclo de vida de una solicitud de reembolso.
public enum RefundStatus {
    REQUESTED,
    APPROVED,
    REJECTED,
    COMPLETED,
    FAILED
}
