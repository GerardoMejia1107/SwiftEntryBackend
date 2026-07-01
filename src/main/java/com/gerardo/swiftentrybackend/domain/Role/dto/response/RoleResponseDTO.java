package com.gerardo.swiftentrybackend.domain.Role.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Representación de un rol devuelta por la API
@Builder
public class RoleResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
