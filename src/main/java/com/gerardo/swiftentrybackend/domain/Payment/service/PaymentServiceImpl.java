package com.gerardo.swiftentrybackend.domain.Payment.service;

import com.gerardo.swiftentrybackend.common.exceptions.BadRequestException;
import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Payment.PaymentModel;
import com.gerardo.swiftentrybackend.domain.Payment.dto.request.PaymentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Payment.dto.response.PaymentResponseDTO;
import com.gerardo.swiftentrybackend.domain.Payment.repositories.PaymentRepository;
import com.gerardo.swiftentrybackend.domain.Payment.utils.PaymentMapper;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import com.gerardo.swiftentrybackend.domain.Reservation.repositories.ReservationRepository;
import com.gerardo.swiftentrybackend.domain.Ticket.repositories.TicketRepository;
import com.gerardo.swiftentrybackend.domain.Ticket.utils.TicketMapper;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// Valida y prepara el pago; la mutación atómica de éxito/fallo se delega a PaymentExecutor
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PaymentExecutor paymentExecutor;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO requestDTO, String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        ReservationModel reservation = reservationRepository.findById(requestDTO.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with id: " + requestDTO.getReservationId()));

        // Ownership check → 403
        if (!reservation.getUser()
                .getId()
                .equals(user.getId())) {
            throw new ForbiddenOperationException("This reservation does not belong to you.");
        }

        // Only PENDING reservations are payable (prevents double charging / re-issuing tickets)
        if (!reservation.getStatus()
                .equals(ReservationStatus.PENDING)) {
            throw new ResourceConflictException(
                    "Reservation is not payable in its current status: " + reservation.getStatus() + ".");
        }

        // Expiry check → persist EXPIRED and reject with 400.
        // This method is intentionally NOT @Transactional so this save commits before we throw.
        if (LocalDateTime.now()
                .isAfter(reservation.getExpiresAt())) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
            throw new BadRequestException("The reservation has expired and can no longer be paid.");
        }

        boolean approved = simulatePaymentProcessing();

        // The atomic success/failure mutation runs inside a single transaction (see PaymentExecutor).
        return paymentExecutor.execute(reservation.getId(), requestDTO, approved);
    }

    // Devuelve el historial de pagos del usuario, con sus tickets asociados
    @Override
    public List<PaymentResponseDTO> getMyPayments(String userEmail) {
        List<PaymentModel> payments = paymentRepository.findByReservation_User_Email(userEmail);
        return payments.stream()
                .map(payment -> paymentMapper.toResponse(
                        payment,
                        ticketMapper.toResponseList(
                                ticketRepository.findByReservationId(payment.getReservation().getId()))))
                .toList();
    }

    // Simula la respuesta de una pasarela de pago externa (siempre aprueba)
    private boolean simulatePaymentProcessing() {
        return true;
    }
}
