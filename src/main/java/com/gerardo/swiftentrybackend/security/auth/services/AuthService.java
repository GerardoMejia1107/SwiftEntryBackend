package com.gerardo.swiftentrybackend.security.auth.services;

import com.gerardo.swiftentrybackend.security.auth.dto.AuthRequestDTO;
import com.gerardo.swiftentrybackend.security.auth.dto.AuthResponseDTO;
import com.gerardo.swiftentrybackend.security.auth.dto.RefreshRequestDTO;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO authRequestDTO);
    AuthResponseDTO refresh(RefreshRequestDTO refreshRequestDTO);
    void logout(RefreshRequestDTO refreshRequestDTO);
}
