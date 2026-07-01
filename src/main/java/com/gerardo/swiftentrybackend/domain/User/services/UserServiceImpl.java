package com.gerardo.swiftentrybackend.domain.User.services;

import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Address.repositories.AddressRepository;
import com.gerardo.swiftentrybackend.domain.Address.utils.AddressMapper;
import com.gerardo.swiftentrybackend.domain.Role.models.RoleModel;
import com.gerardo.swiftentrybackend.domain.Role.repositories.RoleRepository;
import com.gerardo.swiftentrybackend.domain.Role.utils.RoleMapper;
import com.gerardo.swiftentrybackend.domain.User.dto.request.UserRequestDTO;
import com.gerardo.swiftentrybackend.domain.User.dto.response.UserResponseDTO;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import com.gerardo.swiftentrybackend.domain.User.utils.UserMapper;
import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

// Implementación de UserService: registro, consulta con control de propiedad, y listado
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;

    private final UserMapper userMapper;
    private final AddressMapper addressMapper;
    private final RoleMapper roleMapper;

    private final PasswordEncoder passwordEncoder;

    // Crea un usuario: valida email único, resuelve el rol, hashea el password con BCrypt (fuerza 12) y guarda
    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("User with this email already exists");
        }

        RoleModel role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        AddressModel address = addressMapper.toModel(request.getAddress());

        String passwordHash = passwordEncoder.encode(request.getPassword());

        UserModel user = userMapper.toModel(
                request,
                address,
                role,
                passwordHash
        );
    
        return userMapper.toResponse(userRepository.save(user));
    }

    // Obtiene un usuario por id; verifica ownership: solo el propio usuario o un ADMINISTRATOR puede verlo
    @Override
    public UserResponseDTO getUserById(Integer id) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        String authenticatedEmail = authentication.getName();

        UserModel authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Authenticated user not found")
                );

        boolean isAdmin = authenticatedUser.getRole()
                .getName()
                .equals("ADMINISTRATOR");
        boolean isSameUser = authenticatedUser.getId()
                .equals(id);

        if (!isAdmin && !isSameUser) {
            throw new ForbiddenOperationException("You do not have permission to access this user");
        }

        UserModel userModel = userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );
        return userMapper.toResponse(userModel);
    }

    // Devuelve todos los usuarios mapeados a DTO
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
