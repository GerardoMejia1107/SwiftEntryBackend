package com.gerardo.swiftentrybackend.domain.Address;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "street_address", length = 255)
    private String streetAddress;

    @Column(length = 100)
    private String neighborhood;

    @Column(length = 100)
    private String municipality;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String country;

    @Column(name = "reference_point", length = 255)
    private String referencePoint;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
