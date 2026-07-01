package com.gerardo.swiftentrybackend.security.auth.services;

import com.gerardo.swiftentrybackend.security.auth.models.RefreshTokenModel;

// Operaciones de negocio para el ciclo de vida de los refresh tokens persistidos en BD
public interface RefreshTokenService {
    // Genera y guarda un nuevo refresh token (UUID) para el usuario, con expiración configurable
    RefreshTokenModel createRefreshToken(String userEmail);
    // Verifica que el token exista y no haya expirado; lo elimina y lanza InvalidTokenException si expiró
    RefreshTokenModel validateRefreshToken(String token);
    // Elimina un refresh token puntual
    void revokeToken(String token);
    // Elimina todos los refresh tokens de un usuario
    void revokeAllForUser(String userEmail);
}
