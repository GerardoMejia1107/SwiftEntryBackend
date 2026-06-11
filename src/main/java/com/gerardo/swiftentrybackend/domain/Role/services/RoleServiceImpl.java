package com.gerardo.swiftentrybackend.domain.Role.services;

import com.gerardo.swiftentrybackend.domain.Role.dto.request.RoleRequestDTO;
import com.gerardo.swiftentrybackend.domain.Role.dto.response.RoleResponseDTO;
import com.gerardo.swiftentrybackend.domain.Role.models.RoleModel;
import com.gerardo.swiftentrybackend.domain.Role.repositories.RoleRepository;
import com.gerardo.swiftentrybackend.domain.Role.utils.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO) {

        if (roleRepository.existsByName(roleRequestDTO.getName())) {
            throw new RuntimeException("Role already exists");
        }

        RoleModel role = roleMapper.toModel(roleRequestDTO);

        return roleMapper.toResponse(
                roleRepository.save(role)
        );
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }
}
