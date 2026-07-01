package com.gerardo.swiftentrybackend.domain.User.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Representación de un usuario devuelta por la API (sin password)
@Builder
public class UserResponseDTO {

    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String dui;
    private String nit;
    private LocalDate birthDate;
    private Boolean isActive;
    private Boolean emailVerified;
    private Integer addressId;
    private Integer roleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}