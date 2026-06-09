package com.gerardo.swiftentrybackend.domain.Role.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDTO {
    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name cannot exceed 50 characters")
    private String name;

    @NotBlank(message = "Role description is required")
    @Size(max = 250, message = "Role description cannot exceed 250 characters")
    private String description;
}
