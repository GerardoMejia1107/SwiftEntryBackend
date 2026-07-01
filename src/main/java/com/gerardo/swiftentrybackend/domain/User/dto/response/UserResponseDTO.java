package com.gerardo.swiftentrybackend.domain.User.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Representación de un usuario devuelta por la API (sin password)
@Builder
@Schema(description = "Representación de un usuario devuelta por la API (sin password)")
public class UserResponseDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Integer id;
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;
    @Schema(description = "Correo electrónico del usuario", example = "usuario@ejemplo.com")
    private String email;
    @Schema(description = "Número de teléfono de contacto", example = "+503 7123-4567")
    private String phoneNumber;
    @Schema(description = "Documento Único de Identidad", example = "01234567-8")
    private String dui;
    @Schema(description = "Número de Identificación Tributaria", example = "0614-010190-101-2")
    private String nit;
    @Schema(description = "Fecha de nacimiento", example = "1995-05-20")
    private LocalDate birthDate;
    @Schema(description = "Indica si la cuenta está activa", example = "true")
    private Boolean isActive;
    @Schema(description = "Indica si el correo del usuario ha sido verificado", example = "false")
    private Boolean emailVerified;
    @Schema(description = "ID de la dirección postal asociada", example = "3")
    private Integer addressId;
    @Schema(description = "ID del rol asignado al usuario", example = "2")
    private Integer roleId;
    @Schema(description = "Fecha y hora de creación del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha y hora de la última actualización del registro", example = "2026-02-01T08:12:00")
    private LocalDateTime updatedAt;
}