package com.gerardo.swiftentrybackend.domain.User.services;

import com.gerardo.swiftentrybackend.domain.User.dto.request.UserRequestDTO;
import com.gerardo.swiftentrybackend.domain.User.dto.response.UserResponseDTO;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import org.springframework.stereotype.Service;

import java.util.List;


// Operaciones de negocio disponibles para el dominio User
public interface UserService {
    // Registra un usuario nuevo (hashea password y valida email/rol únicos)
    UserResponseDTO createUser(UserRequestDTO requestDTO);

    // Obtiene un usuario por id; solo el dueño o un ADMINISTRATOR puede consultarlo
    UserResponseDTO getUserById(Integer id);

    // Lista todos los usuarios registrados
    List<UserResponseDTO> getAllUsers();
}
