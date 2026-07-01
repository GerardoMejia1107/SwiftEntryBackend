package com.gerardo.swiftentrybackend.domain.Role.repositories;

import com.gerardo.swiftentrybackend.domain.Role.models.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a datos de RoleModel
public interface RoleRepository extends JpaRepository<RoleModel, Integer> {
    // Verifica si ya existe un rol con ese nombre (evita duplicados al crear)
    boolean existsByName(String name);
}
