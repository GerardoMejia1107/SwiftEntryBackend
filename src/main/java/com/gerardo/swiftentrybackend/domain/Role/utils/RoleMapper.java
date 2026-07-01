package com.gerardo.swiftentrybackend.domain.Role.utils;

import com.gerardo.swiftentrybackend.domain.Role.dto.request.RoleRequestDTO;
import com.gerardo.swiftentrybackend.domain.Role.dto.response.RoleResponseDTO;
import com.gerardo.swiftentrybackend.domain.Role.models.RoleModel;
import org.springframework.stereotype.Component;

// Convierte entre RoleModel y sus DTOs de request/response
@Component
public class RoleMapper {

    // Construye una nueva entidad RoleModel a partir del DTO de request
    public RoleModel toModel(RoleRequestDTO request) {
        return RoleModel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    // Convierte una entidad RoleModel a su DTO de respuesta
    public RoleResponseDTO toResponse(RoleModel role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}