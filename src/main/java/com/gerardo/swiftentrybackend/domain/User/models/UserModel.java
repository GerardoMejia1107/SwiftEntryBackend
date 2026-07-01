package com.gerardo.swiftentrybackend.domain.User.models;

import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Role.models.RoleModel;
import com.gerardo.swiftentrybackend.security.auth.models.RefreshTokenModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Entidad JPA de usuario; identidad para login (email) y dueño de dirección/refresh tokens
@Builder
public class UserModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "phone_number", length = 25)
    private String phoneNumber;

    @Column(unique = true, length = 20)
    private String dui;

    @Column(unique = true, length = 20)
    private String nit;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

   // Relación 1:1 con cascade ALL + orphanRemoval: la dirección se crea/borra junto con el usuario
   @OneToOne(
           fetch = FetchType.LAZY,
           cascade = CascadeType.ALL,
           orphanRemoval = true
   )
   @JoinColumn(name = "address_id", unique = true)
   private AddressModel addressModel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleModel role;

    // 1:N con RefreshToken; cascade ALL + orphanRemoval limpia los tokens al eliminar el usuario
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RefreshTokenModel> refreshTokens = new ArrayList<>();

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


