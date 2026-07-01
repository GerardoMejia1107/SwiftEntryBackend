package com.gerardo.swiftentrybackend.domain.Refund.service;

import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundRequestDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.response.RefundResponseDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundUpdateDTO;

import java.util.List;

// Operaciones de negocio sobre solicitudes de reembolso de pagos aprobados.
public interface RefundService {

    // Crea una solicitud de reembolso para un pago aprobado, validando propiedad,
    // duplicados, ventana de tiempo (48h antes del evento) y monto máximo.
    RefundResponseDTO createRefundRequest(RefundRequestDTO requestDTO, String userEmail);

    List<RefundResponseDTO> getAllRefunds();

    RefundResponseDTO getRefundById(Integer id);

    // Lista los reembolsos de un pago, validando que el solicitante sea su dueño.
    List<RefundResponseDTO> getRefundsByPaymentId(Integer paymentId, String userEmail);

    List<RefundResponseDTO> getRefundsByStatus(RefundStatus status);

    RefundResponseDTO updateRefund(Integer id, RefundUpdateDTO updateDTO);

    // Aprueba un reembolso REQUESTED; si es total, libera asientos, cancela tickets y
    // marca reserva/pago como reembolsados.
    RefundResponseDTO approveRefund(Integer id);

    // Rechaza un reembolso que está en estado REQUESTED.
    RefundResponseDTO rejectRefund(Integer id);
}
