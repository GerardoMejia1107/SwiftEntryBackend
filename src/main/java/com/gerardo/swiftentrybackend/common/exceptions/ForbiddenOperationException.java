package com.gerardo.swiftentrybackend.common.exceptions;

// Excepción para operaciones no permitidas al usuario actual; el GlobalExceptionHandler la mapea a 403 FORBIDDEN
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
