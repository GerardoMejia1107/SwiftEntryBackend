package com.gerardo.swiftentrybackend.service;

import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import com.gerardo.swiftentrybackend.dto.refund.RefundRequestDTO;
import com.gerardo.swiftentrybackend.dto.refund.RefundResponseDTO;
import com.gerardo.swiftentrybackend.dto.refund.RefundUpdateDTO;

import java.util.List;

public interface RefundService {

    RefundResponseDTO createRefundRequest(RefundRequestDTO requestDTO);

    List<RefundResponseDTO> getAllRefunds();

    RefundResponseDTO getRefundById(Integer id);

    List<RefundResponseDTO> getRefundsByPaymentId(Integer paymentId);

    List<RefundResponseDTO> getRefundsByStatus(RefundStatus status);

    RefundResponseDTO updateRefund(Integer id, RefundUpdateDTO updateDTO);

    RefundResponseDTO approveRefund(Integer id);

    RefundResponseDTO rejectRefund(Integer id);
}
