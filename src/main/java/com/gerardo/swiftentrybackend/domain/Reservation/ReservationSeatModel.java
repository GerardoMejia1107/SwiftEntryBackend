package com.gerardo.swiftentrybackend.domain.Reservation;

import com.gerardo.swiftentrybackend.domain.Seat.LocalitySeatModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Entidad de unión entre una reserva y un asiento de localidad concreto
public class ReservationSeatModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationModel reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "locality_seat_id", nullable = false)
    private LocalitySeatModel localitySeat;

    // Precio congelado al momento de reservar, independiente de cambios posteriores en la localidad
    @Column(name = "price_at_reservation", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtReservation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
