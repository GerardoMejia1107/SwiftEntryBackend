package com.gerardo.swiftentrybackend.domain.Locality;

import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "localities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Entidad que representa una localidad (sección con precio y cupo) dentro de un evento.
public class LocalityModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private EventModel event;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer capacity;

    // Cupo restante de la localidad; debe mantenerse consistente con el estado de los LocalitySeat asociados.
    @Column(name = "available_slots", nullable = false)
    private Integer availableSlots;

    @Column(name = "early_bird_discount_percentage", precision = 5, scale = 2)
    private BigDecimal earlyBirdDiscountPercentage;

    @Column(name = "early_bird_deadline")
    private LocalDateTime earlyBirdDeadline;

    // Bloqueo optimista para evitar condiciones de carrera al actualizar availableSlots concurrentemente.
    @Version
    @Builder.Default
    @Column(nullable = false)
    private Long version = 0L;

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