package com.gerardo.swiftentrybackend.domain.Reservation.utils;

import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationSeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.LocalitySeatModel;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.response.ReservationSeatResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ReservationSeatMapper {

    public ReservationSeatModel toModel(
            ReservationModel reservation,
            LocalitySeatModel localitySeat,
            BigDecimal priceAtReservation
    ) {
        return ReservationSeatModel.builder()
                .reservation(reservation)
                .localitySeat(localitySeat)
                .priceAtReservation(priceAtReservation)
                .build();
    }

    public ReservationSeatResponseDTO toResponse(ReservationSeatModel model) {
        LocalitySeatModel localitySeat = model.getLocalitySeat();

        return ReservationSeatResponseDTO.builder()
                .id(model.getId())
                .reservationId(model.getReservation().getId())
                .seatId(localitySeat.getSeat().getId())
                .seatNumber(localitySeat.getSeat().getSeatNumber())
                .rowLabel(localitySeat.getSeat().getRowLabel())
                .localityId(localitySeat.getLocality().getId())
                .localityName(localitySeat.getLocality().getName())
                .priceAtReservation(model.getPriceAtReservation())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
