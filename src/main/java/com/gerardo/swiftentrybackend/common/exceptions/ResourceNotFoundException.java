package com.gerardo.swiftentrybackend.common.exceptions;

// Excepción para recursos inexistentes; el GlobalExceptionHandler la mapea a 404 NOT_FOUND
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
