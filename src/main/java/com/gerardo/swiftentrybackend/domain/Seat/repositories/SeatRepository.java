package com.gerardo.swiftentrybackend.domain.Seat.repositories;

import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<SeatModel, Long> {

    Optional<SeatModel> findByRowLabelAndSeatNumber(String rowLabel, String seatNumber);
}
