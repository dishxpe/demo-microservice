package com.collection.univapi.api.exception;

import com.collection.univapi.api.service.audit.AuditService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final AuditService auditService;

    public GlobalExceptionHandler(AuditService auditService) {
        this.auditService = auditService;
    }



    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFileNotFound(FileNotFoundException ex, HttpServletRequest request) {
        return logAndRespond("File not found", ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurity(SecurityException ex, HttpServletRequest request) {
        return logAndRespond("Access denied", ex, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity<?> handleIO(UncheckedIOException ex, HttpServletRequest request) {
        return logAndRespond("File system error", ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        return logAndRespond("Invalid Base64 data", ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBase64(IllegalArgumentException ex, HttpServletRequest request) {
        return logAndRespond("Unexpected error occurred", ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(IOException ex, HttpServletRequest request) {
        return logAndRespond("File operation failed", ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
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


