package com.gerardo.swiftentrybackend.domain.Ticket.utils;

import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.Ticket.TicketTransferModel;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketTransferResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketUpdateDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// Convierte entre TicketModel/TicketTransferModel y sus DTOs de request/response.
@Component
public class TicketMapper {

    // Construye un nuevo ticket a partir de la reserva, el asiento y los códigos generados.
    public TicketModel toModel(
            ReservationModel reservation,
            SeatModel seat,
            String ticketCode,
            String qrCode,
            TicketStatus status,
            LocalDateTime issuedAt
    ) {
        return TicketModel.builder()
                .reservation(reservation)
                .seat(seat)
                .ticketCode(ticketCode)
                .qrCode(qrCode)
                .status(status)
                .issuedAt(issuedAt)
                .build();
    }

    // Mapea el modelo a DTO; resuelve el email del titular actual (poseedor por
    // transferencia o comprador original si nunca fue transferido).
    public TicketResponseDTO toResponse(TicketModel model) {
        return TicketResponseDTO.builder()
                .id(model.getId())
                .reservationId(model.getReservation().getId())
                .seatId(model.getSeat().getId())
                .seatNumber(model.getSeat().getSeatNumber())
                .rowLabel(model.getSeat().getRowLabel())
                .currentHolderEmail(
                        model.getCurrentHolder() != null
                                ? model.getCurrentHolder().getEmail()
                                : model.getReservation().getUser().getEmail()
                )
                .ticketCode(model.getTicketCode())
                .qrCode(model.getQrCode())
                .status(model.getStatus())
                .issuedAt(model.getIssuedAt())
                .usedAt(model.getUsedAt())
                .validatedById(
                        model.getValidatedBy() != null
                                ? model.getValidatedBy().getId()
                                : null
                )
                .validatedByName(
                        model.getValidatedBy() != null
                                ? model.getValidatedBy().getName()
                                : null
                )
                .validatedAt(model.getValidatedAt())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    // Mapea un registro de transferencia (con el ticket embebido) a su DTO de respuesta.
    public TicketTransferResponseDTO toTransferResponse(TicketTransferModel transfer) {
        return TicketTransferResponseDTO.builder()
                .transferId(transfer.getId())
                .transferredAt(transfer.getTransferredAt())
                .fromUserEmail(transfer.getFromUser().getEmail())
                .toUserEmail(transfer.getToUser().getEmail())
                .ticket(toResponse(transfer.getTicket()))
                .build();
    }

    // Variante que además incluye el nombre del evento y de la localidad en la respuesta.
    public TicketResponseDTO toResponse(TicketModel model, String eventName, String localityName) {
        TicketResponseDTO dto = toResponse(model);
        dto.setEventName(eventName);
        dto.setLocalityName(localityName);
        return dto;
    }

    public List<TicketResponseDTO> toResponseList(List<TicketModel> tickets) {
        return tickets.stream()
                .map(this::toResponse)
                .toList();
    }

    // Aplica cambios parciales (solo campos no nulos) del DTO al modelo existente.
    public void updateModel(TicketModel model, TicketUpdateDTO dto, UserModel validatedBy) {
        if (dto.getStatus() != null) {
            model.setStatus(dto.getStatus());
        }

        if (dto.getUsedAt() != null) {
            model.setUsedAt(dto.getUsedAt());
        }

        if (validatedBy != null) {
            model.setValidatedBy(validatedBy);
        }

        if (dto.getValidatedAt() != null) {
            model.setValidatedAt(dto.getValidatedAt());
        }
    }
}
