package com.gerardo.swiftentrybackend.domain.User.services;

import com.gerardo.swiftentrybackend.domain.User.dto.request.UserRequestDTO;
import com.gerardo.swiftentrybackend.domain.User.dto.response.UserResponseDTO;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    UserResponseDTO createUser(UserRequestDTO requestDTO);

    UserResponseDTO getUserById(Integer id);

    List<UserResponseDTO> getAllUsers();
}
