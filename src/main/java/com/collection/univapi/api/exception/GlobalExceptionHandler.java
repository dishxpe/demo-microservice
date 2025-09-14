package com.collection.univapi.api.exception;

import com.collection.univapi.api.service.audit.AuditService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final AuditService auditService;

    public GlobalExceptionHandler(AuditService auditService) {
        this.auditService = auditService;
    }



    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFileNotFound(FileNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", "error",
                        "message", "File not found",
                        "details", ex.getMessage()
                ));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurity(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity<?> handleIO(UncheckedIOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "File system error: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", "error",
                        "message", "Unexpected error occurred",
                        "details", ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBase64(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", "error",
                        "message", "Invalid Base64 data",
                        "details", ex.getMessage()
                ));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(IOException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", "error",
                        "message", "File operation failed",
                        "details", ex.getMessage()
                ));
    }

    // Common method to log and respond safely
    private ResponseEntity<Map<String, Object>> logAndRespond(String userMessage, Exception ex, HttpServletRequest request, HttpStatus status) {
        // Audit/log the error
        auditService.log(
                "internal", // Replace with appId from headers if needed
                request.getRequestURI(),
                request.getMethod(),
                status.value(),
                ex.getMessage()
        );

        // Safe response to user
        Map<String, Object> body = new HashMap<>();
        body.put("message", userMessage);

        return new ResponseEntity<>(body, status);
    }
}


