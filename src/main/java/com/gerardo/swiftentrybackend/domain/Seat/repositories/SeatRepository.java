package com.gerardo.swiftentrybackend.domain.Seat.repositories;

import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<SeatModel, Long> {
    boolean existsBySeatNumberAndLocality_Id(String seatNumber, Long localityId);

    boolean existsByLocality_Id(Long localityId);

    List<SeatModel> findAllByLocality_Id(Long localityId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s from SeatModel s WHERE s.id IN :ids")
    List<SeatModel> findAllByIdWithLock(@Param("ids") List<Long> ids);
}
