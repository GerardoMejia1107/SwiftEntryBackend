package com.gerardo.swiftentrybackend.security.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String tokenType;
    private String role;
}
