package com.gerardo.swiftentrybackend.domain.User.repositories;

import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Acceso a datos de UserModel
public interface UserRepository extends JpaRepository<UserModel, Integer> {

    // Verifica si ya existe un usuario con ese correo (evita duplicados al registrar)
    boolean existsByEmail(String email);
    // Busca un usuario por email; usado para autenticación (JWT subject = email)
    Optional<UserModel> findByEmail(String email);
}
