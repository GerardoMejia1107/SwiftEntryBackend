package com.gerardo.swiftentrybackend.security.auth.services;

import com.gerardo.swiftentrybackend.common.exceptions.InvalidTokenException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import com.gerardo.swiftentrybackend.security.auth.models.RefreshTokenModel;
import com.gerardo.swiftentrybackend.security.auth.repositories.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-expiration-days}")
    private int refreshExpirationDays;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RefreshTokenModel createRefreshToken(String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        RefreshTokenModel refreshToken = RefreshTokenModel.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(refreshExpirationDays))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshTokenModel validateRefreshToken(String token) {
        RefreshTokenModel refreshToken = refreshTokenRepository.findByTokenWithUser(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found or already revoked"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void revokeAllForUser(String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        refreshTokenRepository.deleteAllByUser(user);
    }
}
