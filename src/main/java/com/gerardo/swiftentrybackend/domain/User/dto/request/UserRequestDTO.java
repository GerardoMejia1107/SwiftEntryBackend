package com.gerardo.swiftentrybackend.domain.User.dto.request;

import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Datos de entrada para registrar un usuario, incluye su dirección anidada
@Builder
@Schema(description = "Datos de entrada para registrar un usuario, incluye su dirección anidada")
public class UserRequestDTO {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 150)
    @Schema(description = "Correo electrónico, se usa como identidad en el JWT", example = "usuario@ejemplo.com")
    private String email;

    @Size(max = 25)
    @Schema(description = "Número de teléfono de contacto", example = "+503 7123-4567")
    private String phoneNumber;

    @Size(max = 20)
    @Schema(description = "Documento Único de Identidad", example = "01234567-8")
    private String dui;

    @Size(max = 20)
    @Schema(description = "Número de Identificación Tributaria", example = "0614-010190-101-2")
    private String nit;

    @Past
    @Schema(description = "Fecha de nacimiento", example = "1995-05-20")
    private LocalDate birthDate;

    @NotBlank
    @Size(min = 8, max = 100)
    @Schema(description = "Contraseña en texto plano; se hashea con BCrypt antes de persistir", example = "SuperSecreta123")
    private String password;

    @Valid
    @NotNull
    @Schema(description = "Dirección postal del usuario")
    private AddressRequestDTO address;

    @NotNull
    @Schema(description = "ID del rol a asignar al usuario", example = "2")
    private Integer roleId;
}