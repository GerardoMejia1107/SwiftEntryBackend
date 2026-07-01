package com.gerardo.swiftentrybackend.domain.Reservation.scheduler;

import com.gerardo.swiftentrybackend.domain.Reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Tarea programada que libera asientos de reservas PENDING abandonadas
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

    private final ReservationService reservationService;

    // Corre cada 60 segundos: marca como EXPIRED las reservas PENDING vencidas y libera sus asientos
    @Scheduled(fixedDelay = 60_000)
    public void expireStaleReservations() {
        int count = reservationService.expirePendingReservations();
        if (count > 0) {
            log.info("Expired {} stale reservation(s) and released their seats.", count);
        }
    }
}
