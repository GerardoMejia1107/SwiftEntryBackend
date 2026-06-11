package com.gerardo.swiftentrybackend.security.auth.services;

import com.gerardo.swiftentrybackend.security.auth.models.RefreshTokenModel;

public interface RefreshTokenService {
    RefreshTokenModel createRefreshToken(String userEmail);
    RefreshTokenModel validateRefreshToken(String token);
    void revokeToken(String token);
    void revokeAllForUser(String userEmail);
}
