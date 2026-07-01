package com.gerardo.swiftentrybackend.common.exceptions;

// Excepción para tokens JWT/refresh inválidos o expirados; el GlobalExceptionHandler la mapea a 401 UNAUTHORIZED
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
