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

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    @Column(name = "notification_expires_at")
    private LocalDateTime notificationExpiresAt;

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
