package com.gerardo.swiftentrybackend.common.exceptions;

// Excepción para solicitudes inválidas; el GlobalExceptionHandler la mapea a 400 BAD_REQUEST
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
