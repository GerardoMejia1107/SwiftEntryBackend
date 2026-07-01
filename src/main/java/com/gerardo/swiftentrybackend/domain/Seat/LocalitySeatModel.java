package com.gerardo.swiftentrybackend.domain.Seat;

import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "locality_seats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"seat_id", "locality_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Entidad de unión entre un asiento físico (SeatModel) y una localidad (LocalityModel); su status es la fuente de verdad de disponibilidad.
public class LocalitySeatModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "locality_id", nullable = false)
    private LocalityModel locality;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private SeatModel seat;

    // Estado actual del asiento (AVAILABLE/RESERVED/SOLD); es la fuente de verdad de disponibilidad, no availableSlots por sí solo.
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SeatStatus status = SeatStatus.AVAILABLE;

    // Usuario que mantiene la reserva temporal del asiento mientras está en estado RESERVED.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_by_user_id")
    private UserModel reservedByUser;

    @Column(name = "reservation_expires_at")
    private LocalDateTime reservationExpiresAt;

    @Column(name = "qr_hash", unique = true, length = 64)
    private String qrHash;

    // Bloqueo optimista para evitar condiciones de carrera al reservar/vender el mismo asiento concurrentemente.
    @Version
    @Builder.Default
    @Column(nullable = false)
    private Long version = 0L;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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
