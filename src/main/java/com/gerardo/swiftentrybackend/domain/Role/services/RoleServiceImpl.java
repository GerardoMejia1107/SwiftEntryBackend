package com.gerardo.swiftentrybackend.domain.Role.services;

import com.gerardo.swiftentrybackend.domain.Role.dto.request.RoleRequestDTO;
import com.gerardo.swiftentrybackend.domain.Role.dto.response.RoleResponseDTO;
import com.gerardo.swiftentrybackend.domain.Role.models.RoleModel;
import com.gerardo.swiftentrybackend.domain.Role.repositories.RoleRepository;
import com.gerardo.swiftentrybackend.domain.Role.utils.RoleMapper;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Implementación de RoleService respaldada por RoleRepository
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    // Crea un rol tras validar que el nombre no esté ya en uso (ResourceConflictException si existe)
    @Override
    public RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO) {

        if (roleRepository.existsByName(roleRequestDTO.getName())) {
            throw new ResourceConflictException("Role already exists");
        }

        RoleModel role = roleMapper.toModel(roleRequestDTO);

        return roleMapper.toResponse(
                roleRepository.save(role)
        );
    }

    // Devuelve todos los roles mapeados a DTO
    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }
}
