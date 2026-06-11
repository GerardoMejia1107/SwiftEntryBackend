package com.gerardo.swiftentrybackend.domain.Seat.repositories;

import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<SeatModel, Long> {
    boolean existsBySeatNumberAndLocality_Id(String seatNumber, Long localityId);
    List<SeatModel> findAllByLocality_Id(Long localityId);
}
