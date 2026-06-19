package com.gerardo.swiftentrybackend.domain.Payment.service;

import com.gerardo.swiftentrybackend.common.exceptions.BadRequestException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Payment.PaymentModel;
import com.gerardo.swiftentrybackend.domain.Payment.dto.request.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Payment.dto.response.PaymentResponseDTO;
import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import com.gerardo.swiftentrybackend.domain.Payment.repositories.PaymentRepository;
import com.gerardo.swiftentrybackend.domain.Payment.utils.PaymentMapper;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationSeatModel;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import com.gerardo.swiftentrybackend.domain.Reservation.repositories.ReservationRepository;
import com.gerardo.swiftentrybackend.domain.Seat.LocalitySeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import com.gerardo.swiftentrybackend.domain.Seat.repositories.LocalitySeatRepository;
import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import com.gerardo.swiftentrybackend.domain.Ticket.repositories.TicketRepository;
import com.gerardo.swiftentrybackend.domain.Ticket.utils.TicketMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Holds the atomic payment mutation. Kept in its own bean so the transaction boundary
 * is honoured (Spring proxies external calls), and so the EXPIRED handling in
 * {@link PaymentServiceImpl} can commit before throwing without being rolled back.
 */
@Component
@RequiredArgsConstructor
public class PaymentExecutor {

    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final LocalitySeatRepository localitySeatRepository;
    private final PaymentMapper paymentMapper;
    private final TicketMapper ticketMapper;

    @Transactional
    public PaymentResponseDTO execute(Integer reservationId, PaymentRequestDTO requestDTO, boolean approved) {
        ReservationModel reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with id: " + reservationId));

        // Re-validate inside the transaction — guards against a concurrent thread that
        // also passed the PENDING check in PaymentServiceImpl before either committed.
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ResourceConflictException(
                    "Reservation is not payable in its current status: " + reservation.getStatus() + ".");
        }

        // Re-check expiry inside the transaction: covers the window between the outer
        // check in PaymentServiceImpl and this transaction start. Throwing here rolls
        // back cleanly — no state is written. EXPIRED will be persisted by the outer
        // check on the user's next attempt.
        if (LocalDateTime.now().isAfter(reservation.getExpiresAt())) {
            throw new BadRequestException("The reservation has expired and can no longer be paid.");
        }

        PaymentModel payment = paymentMapper.toModel(
                requestDTO,
                reservation,
                reservation.getTotalAmount(),
                PaymentStatus.PENDING,
                null
        );

        if (!approved) {
            // Failure: record the failed attempt, leave the reservation PENDING so the user can retry
            payment.setStatus(PaymentStatus.FAILED);
            return paymentMapper.toResponse(paymentRepository.save(payment));
        }

        // Success path — everything below runs inside this single transaction
        LocalDateTime now = LocalDateTime.now();

        payment.setStatus(PaymentStatus.APPROVED);
        payment.setPaidAt(now);
        payment.setTransactionReference("TXN-" + UUID.randomUUID());

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setPurchasedAt(now);

        List<LocalitySeatModel> soldSeats = new ArrayList<>();
        List<TicketModel> tickets = new ArrayList<>();

        for (ReservationSeatModel reservationSeat : reservation.getReservationSeats()) {
            LocalitySeatModel localitySeat = reservationSeat.getLocalitySeat();
            localitySeat.setStatus(SeatStatus.SOLD);
            localitySeat.setReservationExpiresAt(null);
            localitySeat.setReservedByUser(null);
            soldSeats.add(localitySeat);

            tickets.add(ticketMapper.toModel(
                    reservation,
                    localitySeat.getSeat(),
                    "TKT-" + UUID.randomUUID(),
                    "QR-" + UUID.randomUUID(),
                    TicketStatus.ISSUED,
                    now
            ));
        }

        localitySeatRepository.saveAll(soldSeats);
        reservationRepository.save(reservation);
        PaymentModel savedPayment = paymentRepository.save(payment);
        List<TicketModel> savedTickets = ticketRepository.saveAll(tickets);

        return paymentMapper.toResponse(savedPayment, ticketMapper.toResponseList(savedTickets));
    }
}
