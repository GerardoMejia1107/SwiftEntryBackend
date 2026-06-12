package com.gerardo.swiftentrybackend.mapper;

import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationSeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import com.gerardo.swiftentrybackend.dto.reservationseat.ReservationSeatResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ReservationSeatMapper {

    public ReservationSeatModel toModel(
            ReservationModel reservation,
            SeatModel seat,
            BigDecimal priceAtReservation
    ) {
        return ReservationSeatModel.builder()
                .reservation(reservation)
                .seat(seat)
                .priceAtReservation(priceAtReservation)
                .build();
    }

    public ReservationSeatResponseDTO toResponse(ReservationSeatModel model) {
        SeatModel seat = model.getSeat();

        return ReservationSeatResponseDTO.builder()
                .id(model.getId())
                .reservationId(model.getReservation().getId())
                .seatId(Math.toIntExact(seat.getId())) // TODO: fix long to int
                .seatNumber(seat.getSeatNumber())
                .rowLabel(seat.getRowLabel())
                .localityId(Math.toIntExact(seat.getLocality().getId())) // TODO: fix long to int
                .localityName(seat.getLocality().getName())
                .priceAtReservation(model.getPriceAtReservation())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
