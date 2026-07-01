package com.gerardo.swiftentrybackend.domain.WaitingList;

import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.WaitingList.enums.WaitingListStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waiting_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Entidad que representa el turno de un usuario en la lista de espera de una localidad sin cupo disponible
public class WaitingListModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "locality_id", nullable = false)
    private LocalityModel locality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WaitingListStatus status;

    // Momento en que se notificó al usuario que hay un cupo disponible
    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    // Límite de tiempo para que el usuario reserve antes de que la notificación expire y se libere el cupo
    @Column(name = "notification_expires_at")
    private LocalDateTime notificationExpiresAt;

    // Momento en que el usuario concretó la reserva tras ser notificado
    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
