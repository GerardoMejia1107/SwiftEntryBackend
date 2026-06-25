package com.gerardo.swiftentrybackend.domain.Ticket.service;

import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationSeatModel;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Reservation.repositories.ReservationRepository;
import com.gerardo.swiftentrybackend.domain.Reservation.repositories.ReservationSeatRepository;
import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.repositories.SeatRepository;
import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.Ticket.TicketTransferModel;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketRequestDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketTransferResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import com.gerardo.swiftentrybackend.domain.Ticket.repositories.TicketRepository;
import com.gerardo.swiftentrybackend.domain.Ticket.repositories.TicketTransferRepository;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationContext;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationHandler;
import com.gerardo.swiftentrybackend.domain.Ticket.utils.TicketMapper;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTransferRepository ticketTransferRepository;
    private final TicketMapper ticketMapper;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final TicketValidationHandler ticketValidationChain;

    @Override
    public TicketResponseDTO createTicket(TicketRequestDTO requestDTO) {
        ReservationModel reservation = reservationRepository.findById(requestDTO.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found: " + requestDTO.getReservationId()));
        SeatModel seat = seatRepository.findById(requestDTO.getSeatId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seat not found: " + requestDTO.getSeatId()));

        TicketModel ticket = ticketMapper.toModel(
                reservation, seat,
                "TKT-" + UUID.randomUUID(),
                "QR-" + UUID.randomUUID(),
                TicketStatus.ISSUED,
                LocalDateTime.now()
        );
        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    public List<TicketResponseDTO> generateTicketsByReservationId(Integer reservationId) {
        return ticketMapper.toResponseList(ticketRepository.findByReservationId(reservationId));
    }

    @Override
    public List<TicketResponseDTO> getAllTickets() {
        return ticketMapper.toResponseList(ticketRepository.findAll());
    }

    @Override
    public TicketResponseDTO getTicketById(Integer id) {
        TicketModel ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id));
        return ticketMapper.toResponse(ticket);
    }

    @Override
    public TicketResponseDTO getTicketByTicketCode(String ticketCode) {
        TicketModel ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with code: " + ticketCode));
        return ticketMapper.toResponse(ticket);
    }

    @Override
    public TicketResponseDTO getTicketByQrCode(String qrCode) {
        TicketModel ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with QR code: " + qrCode));
        return ticketMapper.toResponse(ticket);
    }

    @Override
    public List<TicketResponseDTO> getTicketsByReservationId(Integer reservationId) {
        return ticketMapper.toResponseList(ticketRepository.findByReservationId(reservationId));
    }

    @Override
    public List<TicketResponseDTO> getTicketsByStatus(TicketStatus status) {
        return ticketMapper.toResponseList(ticketRepository.findByStatus(status));
    }

    @Override
    public TicketResponseDTO updateTicket(Integer id, TicketUpdateDTO updateDTO) {
        TicketModel ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id));

        UserModel validatedBy = null;
        if (updateDTO.getValidatedById() != null) {
            validatedBy = userRepository.findById(updateDTO.getValidatedById())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Validator user not found: " + updateDTO.getValidatedById()));
        }

        ticketMapper.updateModel(ticket, updateDTO, validatedBy);
        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    @Transactional
    public TicketResponseDTO validateTicketByQrCode(
            String qrCode,
            Integer validatorUserId) {
        // TODO: probar funcionalidad de validación de tickets

        TicketValidationContext context =
                new TicketValidationContext();

        context.setQrCode(qrCode);
        context.setValidatorUserId(validatorUserId);

        ticketValidationChain.handle(context);

        TicketModel ticket = context.getTicket();
        UserModel validator = context.getValidator();

        LocalDateTime now = LocalDateTime.now();

        ticket.setStatus(TicketStatus.USED);
        ticket.setUsedAt(now);
        ticket.setValidatedAt(now);
        ticket.setValidatedBy(validator);

        ticketRepository.save(ticket);
        // TODO: actualizar ticket a USED

        return ticketMapper.toResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getMyTickets(String userEmail) {
        List<TicketModel> tickets = ticketRepository.findCurrentHolderTickets(userEmail);
        return tickets.stream().map(ticket -> {
            Optional<ReservationSeatModel> rs = reservationSeatRepository
                    .findByReservation_IdAndLocalitySeat_Seat_Id(
                            ticket.getReservation().getId(),
                            ticket.getSeat().getId());
            if (rs.isPresent()) {
                LocalityModel locality = rs.get().getLocalitySeat().getLocality();
                return ticketMapper.toResponse(ticket, locality.getEvent().getName(), locality.getName());
            }
            return ticketMapper.toResponse(ticket);
        }).toList();
    }

    @Override
    @Transactional
    public TicketTransferResponseDTO transferTicket(Integer ticketId, String receiverEmail, String senderEmail) {
        TicketModel ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));

        if (ticket.getStatus() != TicketStatus.ISSUED) {
            throw new ResourceConflictException(
                    "Only ISSUED tickets can be transferred (current status: " + ticket.getStatus() + ").");
        }

        String currentHolderEmail = ticket.getCurrentHolder() != null
                ? ticket.getCurrentHolder().getEmail()
                : ticket.getReservation().getUser().getEmail();

        if (!currentHolderEmail.equalsIgnoreCase(senderEmail)) {
            throw new ForbiddenOperationException("You are not the current holder of this ticket.");
        }

        if (senderEmail.equalsIgnoreCase(receiverEmail)) {
            throw new ResourceConflictException("You cannot transfer a ticket to yourself.");
        }

        UserModel sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Sender user not found: " + senderEmail));

        UserModel receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account found with email: " + receiverEmail));

        // Rotate codes so old QR/ticket codes are immediately invalidated
        ticket.setTicketCode("TKT-" + UUID.randomUUID());
        ticket.setQrCode("QR-" + UUID.randomUUID());
        ticket.setCurrentHolder(receiver);
        ticketRepository.save(ticket);

        TicketTransferModel transfer = TicketTransferModel.builder()
                .ticket(ticket)
                .fromUser(sender)
                .toUser(receiver)
                .transferredAt(LocalDateTime.now())
                .build();
        ticketTransferRepository.save(transfer);

        return ticketMapper.toTransferResponse(transfer);
    }
}
