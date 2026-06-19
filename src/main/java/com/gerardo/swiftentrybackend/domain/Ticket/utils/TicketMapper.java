package com.gerardo.swiftentrybackend.domain.Ticket.utils;

import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import com.gerardo.swiftentrybackend.domain.Ticket.TicketModel;
import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.request.TicketUpdateDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TicketMapper {

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

    public TicketResponseDTO toResponse(TicketModel model) {
        return TicketResponseDTO.builder()
                .id(model.getId())
                .reservationId(model.getReservation().getId())
                .seatId(model.getSeat().getId())
                .seatNumber(model.getSeat().getSeatNumber())
                .rowLabel(model.getSeat().getRowLabel())
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
