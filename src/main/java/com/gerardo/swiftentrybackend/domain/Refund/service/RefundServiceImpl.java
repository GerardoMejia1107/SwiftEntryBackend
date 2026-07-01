package com.gerardo.swiftentrybackend.domain.Refund.service;

import com.gerardo.swiftentrybackend.common.exceptions.BadRequestException;
import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.repositories.LocalityRepository;
import com.gerardo.swiftentrybackend.domain.Payment.PaymentModel;
import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import com.gerardo.swiftentrybackend.domain.Payment.repositories.PaymentRepository;
import com.gerardo.swiftentrybackend.domain.Refund.RefundModel;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundRequestDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.request.RefundUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Refund.dto.response.RefundResponseDTO;
import com.gerardo.swiftentrybackend.domain.Refund.enums.RefundStatus;
import com.gerardo.swiftentrybackend.domain.Refund.repositories.RefundRepository;
import com.gerardo.swiftentrybackend.domain.Refund.utils.RefundMapper;
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
import com.gerardo.swiftentrybackend.domain.WaitingList.service.WaitingListService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Implementación de RefundService: creación, aprobación/rechazo y consulta de reembolsos.
@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
    private final RefundMapper refundMapper;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final LocalitySeatRepository localitySeatRepository;
    private final LocalityRepository localityRepository;
    private final WaitingListService waitingListService;

    // Valida que el pago esté APROBADO, sea del solicitante, no tenga un reembolso
    // pendiente, esté dentro de la ventana de 48h antes del evento y el monto no exceda lo pagado.
    @Override
    public RefundResponseDTO createRefundRequest(RefundRequestDTO dto, String userEmail) {
        PaymentModel payment = paymentRepository.findByIdWithFullChain(dto.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + dto.getPaymentId()));

        if (payment.getStatus() != PaymentStatus.APPROVED) {
            throw new ResourceConflictException("Refunds can only be requested for approved payments");
        }

        if (!payment.getReservation().getUser().getEmail().equals(userEmail)) {
            throw new ForbiddenOperationException("You are not the owner of this payment");
        }

        boolean hasPendingRefund =
                refundRepository.existsByPaymentIdAndStatus(dto.getPaymentId(), RefundStatus.REQUESTED) ||
                refundRepository.existsByPaymentIdAndStatus(dto.getPaymentId(), RefundStatus.APPROVED);
        if (hasPendingRefund) {
            throw new ResourceConflictException("A pending refund already exists for this payment");
        }

        LocalDateTime eventStart = payment.getReservation()
                .getReservationSeats().get(0)
                .getLocalitySeat().getLocality().getEvent().getStartDate();
        if (LocalDateTime.now().isAfter(eventStart.minusHours(48))) {
            throw new BadRequestException("Refund window has closed — refunds must be requested at least 48 hours before the event");
        }

        if (dto.getAmount().compareTo(payment.getAmount()) > 0) {
            throw new BadRequestException("Refund amount cannot exceed the original payment amount of " + payment.getAmount());
        }

        RefundModel refund = refundMapper.toModel(dto, payment, RefundStatus.REQUESTED, null);
        return refundMapper.toResponse(refundRepository.save(refund));
    }

    // Si el reembolso cubre el monto total del pago, cancela los tickets, libera los
    // asientos y marca reserva/pago como reembolsados; en reembolso parcial solo se completa el reembolso.
    @Override
    @Transactional
    public RefundResponseDTO approveRefund(Integer id) {
        RefundModel refund = refundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found with id: " + id));

        if (refund.getStatus() != RefundStatus.REQUESTED) {
            throw new ResourceConflictException("Only refunds in REQUESTED status can be approved");
        }

        PaymentModel payment = refund.getPayment();
        ReservationModel reservation = payment.getReservation();

        boolean isFullRefund = refund.getAmount().compareTo(payment.getAmount()) == 0;

        if (isFullRefund) {
            List<TicketModel> tickets = ticketRepository.findByReservationId(reservation.getId());
            tickets.forEach(ticket -> {
                if (ticket.getStatus() == TicketStatus.ISSUED) {
                    ticket.setStatus(TicketStatus.REFUNDED);
                }
            });
            ticketRepository.saveAll(tickets);

            releaseSeats(reservation);

            reservation.setStatus(ReservationStatus.REFUNDED);
            reservationRepository.save(reservation);

            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        }

        refund.setStatus(RefundStatus.COMPLETED);
        refund.setRefundedAt(LocalDateTime.now());
        return refundMapper.toResponse(refundRepository.save(refund));
    }

    // Rechaza un reembolso en estado REQUESTED sin afectar asientos, reserva ni pago.
    @Override
    public RefundResponseDTO rejectRefund(Integer id) {
        RefundModel refund = refundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found with id: " + id));

        if (refund.getStatus() != RefundStatus.REQUESTED) {
            throw new ResourceConflictException("Only refunds in REQUESTED status can be rejected");
        }

        refund.setStatus(RefundStatus.REJECTED);
        return refundMapper.toResponse(refundRepository.save(refund));
    }

    @Override
    public RefundResponseDTO updateRefund(Integer id, RefundUpdateDTO updateDTO) {
        RefundModel refund = refundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found with id: " + id));

        refundMapper.updateModel(refund, updateDTO);
        return refundMapper.toResponse(refundRepository.save(refund));
    }

    @Override
    public List<RefundResponseDTO> getAllRefunds() {
        return refundMapper.toResponseList(refundRepository.findAll());
    }

    @Override
    public RefundResponseDTO getRefundById(Integer id) {
        return refundRepository.findById(id)
                .map(refundMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found with id: " + id));
    }

    @Override
    public List<RefundResponseDTO> getRefundsByPaymentId(Integer paymentId, String userEmail) {
        PaymentModel payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (!payment.getReservation().getUser().getEmail().equals(userEmail)) {
            throw new ForbiddenOperationException("You are not the owner of this payment");
        }

        return refundMapper.toResponseList(refundRepository.findByPaymentId(paymentId));
    }

    @Override
    public List<RefundResponseDTO> getRefundsByStatus(RefundStatus status) {
        return refundMapper.toResponseList(refundRepository.findByStatus(status));
    }

    // Libera los asientos de una reserva reembolsada, restaura los cupos de cada
    // localidad y notifica a la lista de espera de los cupos liberados.
    private void releaseSeats(ReservationModel reservation) {
        List<LocalitySeatModel> seats = reservation.getReservationSeats()
                .stream()
                .map(ReservationSeatModel::getLocalitySeat)
                .toList();

        Map<Long, LocalityModel> localityMap = new HashMap<>();
        Map<Long, Integer> restoreDeltas = new HashMap<>();

        for (LocalitySeatModel ls : seats) {
            ls.setStatus(SeatStatus.AVAILABLE);
            ls.setReservedByUser(null);
            ls.setReservationExpiresAt(null);

            LocalityModel loc = ls.getLocality();
            localityMap.put(loc.getId(), loc);
            restoreDeltas.merge(loc.getId(), 1, Integer::sum);
        }

        localitySeatRepository.saveAll(seats);

        localityMap.forEach((locId, loc) ->
                loc.setAvailableSlots(loc.getAvailableSlots() + restoreDeltas.get(locId))
        );
        localityRepository.saveAll(new ArrayList<>(localityMap.values()));

        restoreDeltas.forEach((locId, delta) ->
                waitingListService.notifyNextInQueue(locId, delta));
    }
}
