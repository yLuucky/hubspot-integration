package com.integration.hubspot.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HubspotApiException.class)
    public ResponseEntity<ErrorResponse> handleHubspotApiException(HubspotApiException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatus().value(),
                ex.getErrorCode(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = new ValidationErrorResponse(
                400,
                "VALIDATION_ERROR",
                "Input data validation error",
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "INTERNAL_SERVER_ERROR",
                "An internal error occurred: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @Data
    @AllArgsConstructor
    private static class ErrorResponse {
        private int status;
        private String code;
        private String message;
        private LocalDateTime timestamp;
    }

    @Data
    @AllArgsConstructor
    private static class ValidationErrorResponse {
        private int status;
        private String code;
        private String message;
        private LocalDateTime timestamp;
        private Map<String, String> errors;
    }
}
