package com.gerardo.swiftentrybackend.domain.User.dto.request;

import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
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
public class UserRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 25)
    private String phoneNumber;

    @Size(max = 20)
    private String dui;

    @Size(max = 20)
    private String nit;

    @Past
    private LocalDate birthDate;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @Valid
    @NotNull
    private AddressRequestDTO address;

    @NotNull
    private Integer roleId;
}