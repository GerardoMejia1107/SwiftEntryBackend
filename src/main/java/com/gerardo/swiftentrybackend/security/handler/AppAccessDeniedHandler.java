package com.gerardo.swiftentrybackend.security.handler;

import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AppAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        GeneralResponse body = GeneralResponse.builder()
                .uri(request.getRequestURI())
                .message("Access denied: you do not have the required role for this resource")
                .status(HttpStatus.FORBIDDEN.value())
                .build();

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
