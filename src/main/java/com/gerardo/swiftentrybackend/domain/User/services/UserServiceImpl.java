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
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        RoleModel role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

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

    @Override
    public UserResponseDTO getUserById(Integer id) {
        UserModel userModel = userRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("User with id " + id + " not found")
                );
        return userMapper.toResponse(userModel);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
