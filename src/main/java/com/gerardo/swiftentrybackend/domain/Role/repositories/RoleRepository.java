package com.gerardo.swiftentrybackend.domain.Role.repositories;

import com.gerardo.swiftentrybackend.domain.Role.models.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleModel, Integer> {
    boolean existsByName(String name);
}
