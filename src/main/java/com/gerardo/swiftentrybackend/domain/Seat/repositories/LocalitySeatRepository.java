package com.gerardo.swiftentrybackend.domain.Seat.repositories;

import com.gerardo.swiftentrybackend.domain.Seat.LocalitySeatModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocalitySeatRepository extends JpaRepository<LocalitySeatModel, Long> {

    List<LocalitySeatModel> findAllByLocality_Id(Long localityId);

    List<LocalitySeatModel> findAllByLocality_Event_Id(Integer eventId);

    boolean existsByLocality_Id(Long localityId);

    boolean existsBySeat_IdAndLocality_Event_Id(Long seatId, Integer eventId);

    boolean existsBySeat_IdAndLocality_Id(Long seatId, Long localityId);

    @Modifying
    @Query("DELETE FROM LocalitySeatModel ls WHERE ls.locality.id = :localityId")
    void deleteAllByLocality_Id(@Param("localityId") Long localityId);

    @Modifying
    @Query("DELETE FROM LocalitySeatModel ls WHERE ls.seat.id = :seatId")
    void deleteAllBySeat_Id(@Param("seatId") Long seatId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ls FROM LocalitySeatModel ls JOIN FETCH ls.locality JOIN FETCH ls.seat WHERE ls.id IN :ids")
    List<LocalitySeatModel> findAllByIdWithLock(@Param("ids") List<Long> ids);

    @Query("""
    SELECT
        e.id AS eventId,
        e.name AS eventName,
        SUM(l.capacity) AS totalSeats,
        SUM(l.availableSlots) AS availableSeats
    FROM LocalityModel l
    JOIN l.event e
    GROUP BY e.id, e.name
    """)
    List<com.gerardo.swiftentrybackend.domain.Report.EventAvailabilityProjection> getSeatAvailabilityReport();
}
