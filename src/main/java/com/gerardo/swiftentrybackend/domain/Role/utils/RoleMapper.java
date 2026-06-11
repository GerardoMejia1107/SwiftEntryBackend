package com.gerardo.swiftentrybackend.domain.Role.utils;

import com.gerardo.swiftentrybackend.domain.Role.dto.request.RoleRequestDTO;
import com.gerardo.swiftentrybackend.domain.Role.dto.response.RoleResponseDTO;
import com.gerardo.swiftentrybackend.domain.Role.models.RoleModel;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleModel toModel(RoleRequestDTO request) {
        return RoleModel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

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