package com.gerardo.swiftentrybackend.domain.Seat.repositories;

import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repositorio JPA de asientos físicos.
public interface SeatRepository extends JpaRepository<SeatModel, Long> {

    // Busca un asiento físico por su fila y número.
    Optional<SeatModel> findByRowLabelAndSeatNumber(String rowLabel, String seatNumber);
}
