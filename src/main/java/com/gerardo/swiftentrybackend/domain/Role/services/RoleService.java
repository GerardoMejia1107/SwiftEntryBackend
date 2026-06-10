package com.gerardo.swiftentrybackend.domain.Role.services;


import com.gerardo.swiftentrybackend.domain.Role.dto.request.RoleRequestDTO;
import com.gerardo.swiftentrybackend.domain.Role.dto.response.RoleResponseDTO;

import java.util.List;

public interface RoleService {
    RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO);

    List<RoleResponseDTO> getAllRoles();




}
