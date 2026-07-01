package com.gerardo.swiftentrybackend.security.auth.services;

import com.gerardo.swiftentrybackend.security.auth.dto.AuthRequestDTO;
import com.gerardo.swiftentrybackend.security.auth.dto.AuthResponseDTO;
import com.gerardo.swiftentrybackend.security.auth.dto.RefreshRequestDTO;

// Operaciones de negocio de autenticación: login, refresh y logout
public interface AuthService {
    // Autentica email/password y emite access + refresh token
    AuthResponseDTO login(AuthRequestDTO authRequestDTO);
    // Valida el refresh token, lo rota y emite un nuevo par de tokens
    AuthResponseDTO refresh(RefreshRequestDTO refreshRequestDTO);
    // Revoca el refresh token dado, cerrando la sesión
    void logout(RefreshRequestDTO refreshRequestDTO);
}
