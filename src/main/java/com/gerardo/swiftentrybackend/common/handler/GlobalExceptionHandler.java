package com.gerardo.swiftentrybackend.common.handler;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.common.exceptions.BadRequestException;
import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.common.exceptions.InvalidTokenException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

// Manejador global que traduce excepciones a respuestas GeneralResponse con el status HTTP correspondiente
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ResponseBuilder responseBuilder;

    // Errores de validación de @Valid: junta los errores por campo en un mapa
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                fieldErrors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });
        return responseBuilder.buildResponse("Validation failed", HttpStatus.BAD_REQUEST, fieldErrors);
    }

    // Body de request malformado o ausente
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GeneralResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        return responseBuilder.buildResponse("Malformed or missing request body", HttpStatus.BAD_REQUEST, null);
    }

    // Parámetro de request con un tipo/valor inválido
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GeneralResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'";
        return responseBuilder.buildResponse(msg, HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<GeneralResponse> handleInvalidToken(InvalidTokenException ex) {
        return responseBuilder.buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GeneralResponse> handleBadRequest(BadRequestException ex) {
        return responseBuilder.buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GeneralResponse> handleNotFound(ResourceNotFoundException ex) {
        return responseBuilder.buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    // Optimistic lock contention (e.g. two payments racing on the same seat) → 409
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<GeneralResponse> handleOptimisticLock(OptimisticLockingFailureException ex) {
        return responseBuilder.buildResponse(
                "The resource was modified by another transaction. Please try again.",
                HttpStatus.CONFLICT,
                null
        );
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<GeneralResponse> handleConflict(ResourceConflictException ex) {
        return responseBuilder.buildResponse(ex.getMessage(), HttpStatus.CONFLICT, null);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<GeneralResponse> handleForbiddenOperation(ForbiddenOperationException ex) {
        return responseBuilder.buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN, null);
    }

    // Spring Security deniega el acceso por falta de rol/autoridad
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GeneralResponse> handleAccessDenied(AccessDeniedException ex) {
        return responseBuilder.buildResponse(
                "Access denied: you do not have permission to perform this action",
                HttpStatus.FORBIDDEN,
                null
        );
    }

    // Falla de autenticación genérica de Spring Security
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GeneralResponse> handleAuthentication(AuthenticationException ex) {
        return responseBuilder.buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, null);
    }

    // Fallback para cualquier excepción no manejada explícitamente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse> handleGeneric(Exception ex) {
        return responseBuilder.buildResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, null);
    }
}
