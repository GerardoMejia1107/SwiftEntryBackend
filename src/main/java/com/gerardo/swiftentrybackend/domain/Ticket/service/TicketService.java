package com.gerardo.swiftentrybackend.domain.Ticket.service;

import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketTransferResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketRequestDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketUpdateDTO;

import java.util.List;

// Operaciones de negocio sobre tickets: emisión, consulta, validación de acceso y transferencia.
public interface TicketService {

    // Crea un ticket individual para un asiento de una reserva.
    TicketResponseDTO createTicket(TicketRequestDTO requestDTO);

    // Obtiene todos los tickets generados para una reserva.
    List<TicketResponseDTO> generateTicketsByReservationId(Integer reservationId);

    List<TicketResponseDTO> getAllTickets();

    TicketResponseDTO getTicketById(Integer id);

    TicketResponseDTO getTicketByTicketCode(String ticketCode);

    TicketResponseDTO getTicketByQrCode(String qrCode);

    List<TicketResponseDTO> getTicketsByReservationId(Integer reservationId);

    List<TicketResponseDTO> getTicketsByStatus(TicketStatus status);

    TicketResponseDTO updateTicket(Integer id, TicketUpdateDTO updateDTO);

    // Valida un ticket por código QR (escaneo de acceso); implementado en el servicio
    // pero sin ruta HTTP expuesta.
    TicketResponseDTO validateTicketByQrCode(String qrCode, String validatorEmail);

    // Lista los tickets que el usuario posee actualmente (comprados o recibidos por transferencia).
    List<TicketResponseDTO> getMyTickets(String userEmail);

    // Transfiere la titularidad de un ticket ISSUED de un usuario a otro.
    TicketTransferResponseDTO transferTicket(Integer ticketId, String receiverEmail, String senderEmail);

    // Genera la imagen del código QR del ticket, solo accesible por su poseedor actual.
    byte[] getTicketQrImage(Integer ticketId, String userEmail);
}
