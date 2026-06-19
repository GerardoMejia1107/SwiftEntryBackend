package com.gerardo.swiftentrybackend.domain.Payment.dto.response;

import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentMethod;
import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private Integer id;

    private Integer reservationId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;
    private ReservationStatus reservationStatus;

    private PaymentStatus status;

    private String transactionReference;

    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Tickets issued when the payment is approved (empty otherwise)
    private List<TicketResponseDTO> tickets;
}
