package com.gerardo.swiftentrybackend.security.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Credenciales de entrada para el login (email + password)
@Builder
@Schema(description = "Credenciales de entrada para el login")
public class AuthRequestDTO {
    @NotBlank
    @Email
    @Schema(description = "Correo electrónico del usuario", example = "usuario@ejemplo.com")
    private String email;

    @NotBlank
    @Schema(description = "Contraseña en texto plano", example = "SuperSecreta123")
    private String password;
}
