package com.gerardo.swiftentrybackend.domain.Seat.utils;

import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public SeatResponseDTO toResponse(SeatModel seat) {
        return SeatResponseDTO.builder()
                .id(seat.getId())
                .localityId(seat.getLocality() != null ? seat.getLocality().getId() : null)
                .localityName(seat.getLocality() != null ? seat.getLocality().getName() : null)
                .seatNumber(seat.getSeatNumber())
                .rowLabel(seat.getRowLabel())
                .status(seat.getStatus())
                .isActive(seat.getIsActive())
                .createdAt(seat.getCreatedAt())
                .updatedAt(seat.getUpdatedAt())
                .build();
    }
}