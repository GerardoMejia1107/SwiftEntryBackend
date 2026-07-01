package com.gerardo.swiftentrybackend.domain.Role.services;


import com.gerardo.swiftentrybackend.domain.Role.dto.request.RoleRequestDTO;
import com.gerardo.swiftentrybackend.domain.Role.dto.response.RoleResponseDTO;

import java.util.List;

// Operaciones de negocio disponibles para el dominio Role
public interface RoleService {
    // Crea un rol nuevo; falla si ya existe un rol con el mismo nombre
    RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO);

    // Lista todos los roles registrados
    List<RoleResponseDTO> getAllRoles();




}
