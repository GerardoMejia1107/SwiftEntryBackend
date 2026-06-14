package com.gerardo.swiftentrybackend.security.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserDTO {
    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String role;
}
