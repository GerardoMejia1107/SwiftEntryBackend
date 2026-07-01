package com.gerardo.swiftentrybackend.domain.Seat.utils;

import com.gerardo.swiftentrybackend.domain.Seat.LocalitySeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.LocalitySeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;
import org.springframework.stereotype.Component;

@Component
// Convierte entre SeatModel/LocalitySeatModel y sus DTOs de respuesta.
public class SeatMapper {

    // Convierte un asiento físico a su DTO de respuesta.
    public SeatResponseDTO toResponse(SeatModel seat) {
        return SeatResponseDTO.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .rowLabel(seat.getRowLabel())
                .createdAt(seat.getCreatedAt())
                .updatedAt(seat.getUpdatedAt())
                .build();
    }

    // Convierte una asignación asiento-localidad (LocalitySeat) a su DTO de respuesta.
    public LocalitySeatResponseDTO toLocalitySeatResponse(LocalitySeatModel localitySeat) {
        return LocalitySeatResponseDTO.builder()
                .localitySeatId(localitySeat.getId())
                .seatId(localitySeat.getSeat().getId())
                .seatNumber(localitySeat.getSeat().getSeatNumber())
                .rowLabel(localitySeat.getSeat().getRowLabel())
                .localityId(localitySeat.getLocality().getId())
                .localityName(localitySeat.getLocality().getName())
                .status(localitySeat.getStatus())
                .isActive(localitySeat.getIsActive())
                .createdAt(localitySeat.getCreatedAt())
                .updatedAt(localitySeat.getUpdatedAt())
                .build();
    }
}
