package com.gerardo.swiftentrybackend.service;

import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import com.gerardo.swiftentrybackend.dto.ticket.TicketRequestDTO;
import com.gerardo.swiftentrybackend.dto.ticket.TicketResponseDTO;
import com.gerardo.swiftentrybackend.dto.ticket.TicketUpdateDTO;

import java.util.List;

public interface TicketService {

    TicketResponseDTO createTicket(TicketRequestDTO requestDTO);

    List<TicketResponseDTO> generateTicketsByReservationId(Integer reservationId);

    List<TicketResponseDTO> getAllTickets();

    TicketResponseDTO getTicketById(Integer id);

    TicketResponseDTO getTicketByTicketCode(String ticketCode);

    TicketResponseDTO getTicketByQrCode(String qrCode);

    List<TicketResponseDTO> getTicketsByReservationId(Integer reservationId);

    List<TicketResponseDTO> getTicketsByStatus(TicketStatus status);

    TicketResponseDTO updateTicket(Integer id, TicketUpdateDTO updateDTO);

    TicketResponseDTO validateTicketByQrCode(String qrCode, Integer validatorUserId);
}
