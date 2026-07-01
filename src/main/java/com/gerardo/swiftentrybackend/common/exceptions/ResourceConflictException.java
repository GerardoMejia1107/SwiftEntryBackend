package com.gerardo.swiftentrybackend.common.exceptions;

// Excepción para conflictos de estado de un recurso (ej. duplicados); el GlobalExceptionHandler la mapea a 409 CONFLICT
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
