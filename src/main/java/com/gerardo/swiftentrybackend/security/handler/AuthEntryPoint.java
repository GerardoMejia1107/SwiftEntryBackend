package com.gerardo.swiftentrybackend.security.handler;

import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


import java.io.IOException;

// Responde con el envoltorio GeneralResponse (401) cuando falta o es inválida la autenticación
@Component
@RequiredArgsConstructor
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // Escribe la respuesta JSON 401 en el formato estándar de la API
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        GeneralResponse body = GeneralResponse.builder()
                .uri(request.getRequestURI())
                .message("Authentication required: please provide a valid token")
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
