package com.collection.univapi.api.exception;

import com.collection.univapi.api.service.audit.AuditService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final AuditService auditService;

    public GlobalExceptionHandler(AuditService auditService) {
        this.auditService = auditService;
    }

    // 1. Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex, HttpServletRequest request) {
        return logAndRespond("An unexpected error occurred. Please contact support.", ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 2. Handle not found exceptions
    @ExceptionHandler({NoSuchElementException.class, FileNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(Exception ex, HttpServletRequest request) {
        return logAndRespond("The requested resource was not found.", ex, request, HttpStatus.NOT_FOUND);
    }

    // 3. Handle bad requests
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex, HttpServletRequest request) {
        return logAndRespond("Invalid request parameters.", ex, request, HttpStatus.BAD_REQUEST);
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


