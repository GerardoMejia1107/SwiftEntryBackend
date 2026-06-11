package com.gerardo.swiftentrybackend.security.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshRequestDTO {
    @NotBlank
    private String refreshToken;
}
