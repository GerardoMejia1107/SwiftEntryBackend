package com.gerardo.swiftentrybackend.domain.Ticket.service;

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
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketRequestDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import com.gerardo.swiftentrybackend.domain.Ticket.repositories.TicketRepository;
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
    private final TicketMapper ticketMapper;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

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
    public TicketResponseDTO validateTicketByQrCode(String qrCode, Integer validatorUserId) {
        TicketModel ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with QR code: " + qrCode));

        if (ticket.getStatus() != TicketStatus.ISSUED) {
            throw new ResourceConflictException(
                    "Ticket cannot be validated in its current status: " + ticket.getStatus());
        }

        UserModel validator = userRepository.findById(validatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Validator user not found: " + validatorUserId));

        LocalDateTime now = LocalDateTime.now();
        ticket.setStatus(TicketStatus.USED);
        ticket.setUsedAt(now);
        ticket.setValidatedBy(validator);
        ticket.setValidatedAt(now);

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getMyTickets(String userEmail) {
        List<TicketModel> tickets = ticketRepository.findByReservation_User_Email(userEmail);
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
}
