package com.gerardo.swiftentrybackend.domain.Payment.service;

import com.gerardo.swiftentrybackend.common.exceptions.BadRequestException;
import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
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
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final LocalitySeatRepository localitySeatRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO requestDTO, String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        ReservationModel reservation = reservationRepository.findById(requestDTO.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with id: " + requestDTO.getReservationId()));

        // Ownership check → 403
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("This reservation does not belong to you.");
        }

        // Expiry check → mark EXPIRED and reject with 400
        if (LocalDateTime.now().isAfter(reservation.getExpiresAt())) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
            throw new BadRequestException("The reservation has expired and can no longer be paid.");
        }

        // Only PENDING reservations are payable (prevents double charging / re-issuing tickets)
        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new ResourceConflictException(
                    "Reservation is not payable in its current status: " + reservation.getStatus() + ".");
        }

        // Create the payment record in PENDING state
        PaymentModel payment = paymentMapper.toModel(
                requestDTO,
                reservation,
                reservation.getTotalAmount(),
                PaymentStatus.PENDING,
                null
        );

        boolean approved = simulatePaymentProcessing();

        if (!approved) {
            // Failure: record the failed attempt, leave the reservation PENDING so the user can retry
            payment.setStatus(PaymentStatus.FAILED);
            PaymentModel savedPayment = paymentRepository.save(payment);
            return paymentMapper.toResponse(savedPayment);
        }

        // Success path — everything below runs inside this single transaction
        LocalDateTime now = LocalDateTime.now();

        payment.setStatus(PaymentStatus.APPROVED);
        payment.setPaidAt(now);
        if (payment.getTransactionReference() == null || payment.getTransactionReference().isBlank()) {
            payment.setTransactionReference("TXN-" + UUID.randomUUID());
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setPurchasedAt(now);

        List<LocalitySeatModel> soldSeats = new ArrayList<>();
        List<TicketModel> tickets = new ArrayList<>();

        for (ReservationSeatModel reservationSeat : reservation.getReservationSeats()) {
            LocalitySeatModel localitySeat = reservationSeat.getLocalitySeat();
            localitySeat.setStatus(SeatStatus.SOLD);
            localitySeat.setReservationExpiresAt(null);
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

    /**
     * Simulates an external payment gateway call. Replace with a real integration later.
     * Currently always approves the payment.
     */
    private boolean simulatePaymentProcessing() {
        return true;
    }
}
