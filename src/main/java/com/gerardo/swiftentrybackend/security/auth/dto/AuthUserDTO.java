package com.gerardo.swiftentrybackend.security.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Datos mínimos del usuario embebidos en AuthResponseDTO
@Builder
public class AuthUserDTO {
    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String role;
}
