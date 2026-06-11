package com.gerardo.swiftentrybackend.domain.User.utils;

import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Role.models.RoleModel;
import com.gerardo.swiftentrybackend.domain.User.dto.request.UserRequestDTO;
import com.gerardo.swiftentrybackend.domain.User.dto.response.UserResponseDTO;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserModel toModel(
            UserRequestDTO request,
            AddressModel address,
            RoleModel role,
            String passwordHash
    ) {
        return UserModel.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dui(request.getDui())
                .nit(request.getNit())
                .birthDate(request.getBirthDate())
                .passwordHash(passwordHash)
                .addressModel(address)
                .role(role)
                .build();
    }

    public UserResponseDTO toResponse(UserModel user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dui(user.getDui())
                .nit(user.getNit())
                .birthDate(user.getBirthDate())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .addressId(user.getAddressModel() != null ? user.getAddressModel().getId() : null)
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}