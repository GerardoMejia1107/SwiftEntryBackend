package com.gerardo.swiftentrybackend.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

// Envoltorio estándar de respuesta de la API (uri, mensaje, status, data)
@Data
@Builder
public class GeneralResponse {
    private String uri;
    private String message;
    private int status;
    private LocalDateTime time;
    private Object data;
}
