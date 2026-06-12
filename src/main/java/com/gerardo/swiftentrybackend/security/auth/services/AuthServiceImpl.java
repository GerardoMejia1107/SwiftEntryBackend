package com.gerardo.swiftentrybackend.security.auth.services;

import com.gerardo.swiftentrybackend.config.CustomUserDetailsService;
import com.gerardo.swiftentrybackend.security.auth.dto.AuthRequestDTO;
import com.gerardo.swiftentrybackend.security.auth.dto.AuthResponseDTO;
import com.gerardo.swiftentrybackend.security.auth.dto.RefreshRequestDTO;
import com.gerardo.swiftentrybackend.security.auth.models.RefreshTokenModel;
import com.gerardo.swiftentrybackend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public AuthResponseDTO login(AuthRequestDTO authRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getEmail(),
                        authRequestDTO.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow()
                .getAuthority();

        RefreshTokenModel refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .role(role)
                .build();
    }

    @Override
    public AuthResponseDTO refresh(RefreshRequestDTO refreshRequestDTO) {
        RefreshTokenModel oldToken = refreshTokenService.validateRefreshToken(refreshRequestDTO.getRefreshToken());
        String userEmail = oldToken.getUser().getEmail();

        refreshTokenService.revokeToken(refreshRequestDTO.getRefreshToken());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        String accessToken = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow()
                .getAuthority();

        RefreshTokenModel newRefreshToken = refreshTokenService.createRefreshToken(userEmail);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .role(role)
                .build();
    }

    @Override
    public void logout(RefreshRequestDTO refreshRequestDTO) {
        refreshTokenService.revokeToken(refreshRequestDTO.getRefreshToken());
    }
}
