package com.gerardo.swiftentrybackend.domain.Seat.utils;

import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public SeatModel toModel(SeatRequestDTO request, LocalityModel locality) {
        SeatModel.SeatModelBuilder builder = SeatModel.builder()
                .locality(locality)
                .seatNumber(request.getSeatNumber())
                .rowLabel(request.getRowLabel())
                .qrHash(request.getQrHash());

        if (request.getStatus() != null) {
            builder.status(request.getStatus());
        }

        return builder.build();
    }

    public SeatResponseDTO toResponse(SeatModel seat) {
        return SeatResponseDTO.builder()
                .id(seat.getId())
                .localityId(seat.getLocality() != null ? seat.getLocality().getId() : null)
                .localityName(seat.getLocality() != null ? seat.getLocality().getName() : null)
                .seatNumber(seat.getSeatNumber())
                .rowLabel(seat.getRowLabel())
                .status(seat.getStatus())
                .isActive(seat.getIsActive())
                .qrHash(seat.getQrHash())
                .createdAt(seat.getCreatedAt())
                .updatedAt(seat.getUpdatedAt())
                .build();
    }

    public void updateModel(SeatModel seat, SeatUpdateDTO request) {
        if (request.getSeatNumber() != null) seat.setSeatNumber(request.getSeatNumber());
        if (request.getRowLabel() != null) seat.setRowLabel(request.getRowLabel());
        if (request.getStatus() != null) seat.setStatus(request.getStatus());
        if (request.getQrHash() != null) seat.setQrHash(request.getQrHash());
        if (request.getIsActive() != null) seat.setIsActive(request.getIsActive());
    }
}
