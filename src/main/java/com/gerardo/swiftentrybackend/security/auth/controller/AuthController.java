package com.gerardo.swiftentrybackend.security.auth.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.security.auth.dto.AuthRequestDTO;
import com.gerardo.swiftentrybackend.security.auth.dto.AuthResponseDTO;
import com.gerardo.swiftentrybackend.security.auth.dto.RefreshRequestDTO;
import com.gerardo.swiftentrybackend.security.auth.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Endpoints REST públicos de autenticación: login, refresh y logout
@Tag(name = "Autenticación", description = "Login, refresh de token y logout")
@RestController
@RequiredArgsConstructor
@RequestMapping("/swift_entry/auth")
public class AuthController {

    private final AuthService authService;
    private final ResponseBuilder responseBuilder;

    // Autentica credenciales y emite access token + refresh token
    @Operation(summary = "Iniciar sesión", description = "Retorna un access token (15 min) y un refresh token (7 días)")
    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(
            @Valid @RequestBody AuthRequestDTO requestDTO
    ) {
        AuthResponseDTO authResponseDTO = authService.login(requestDTO);
        return responseBuilder.buildResponse("Login successful", HttpStatus.OK, authResponseDTO);
    }

    // Renueva el access token; el refresh token usado se rota (se invalida y se emite uno nuevo)
    @Operation(summary = "Renovar token", description = "Renueva el access token usando el refresh token. El refresh token se rota en cada llamada")
    @PostMapping("/refresh")
    public ResponseEntity<GeneralResponse> refresh(
            @Valid @RequestBody RefreshRequestDTO requestDTO
    ) {
        AuthResponseDTO authResponseDTO = authService.refresh(requestDTO);
        return responseBuilder.buildResponse("Token refreshed successfully", HttpStatus.OK, authResponseDTO);
    }

    // Elimina el refresh token de la base de datos, cerrando la sesión
    @Operation(summary = "Cerrar sesión", description = "Invalida el refresh token en base de datos")
    @PostMapping("/logout")
    public ResponseEntity<GeneralResponse> logout(
            @Valid @RequestBody RefreshRequestDTO requestDTO
    ) {
        authService.logout(requestDTO);
        return responseBuilder.buildResponse("Logout successful", HttpStatus.OK, null);
    }
}
