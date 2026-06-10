package com.gerardo.swiftentrybackend.security.auth.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.security.auth.dto.AuthRequestDTO;
import com.gerardo.swiftentrybackend.security.auth.dto.AuthResponseDTO;
import com.gerardo.swiftentrybackend.security.auth.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/swift_entry/auth")
public class AuthController {
    private final AuthService authService;
    private final ResponseBuilder responseBuilder;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(
            @Valid @RequestBody AuthRequestDTO requestDTO
    ) {
        AuthResponseDTO authResponseDTO = authService.login(requestDTO);

        return responseBuilder.buildResponse(
                "Login successful",
                HttpStatus.OK,
                authResponseDTO
        );
    }
}
