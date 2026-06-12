package com.gerardo.swiftentrybackend.domain.Refund.service;

import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundRequestDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.response.RefundResponseDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundUpdateDTO;

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
