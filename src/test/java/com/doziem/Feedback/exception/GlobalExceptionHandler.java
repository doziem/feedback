package com.doziem.Feedback.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(LocalDateTime.now(), 400, ex.getMessage()));
    }

    @ExceptionHandler(InvalidFeedbackException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFeedback(InvalidFeedbackException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(LocalDateTime.now(), 400, ex.getMessage()));
    }
}

record ErrorResponse(LocalDateTime timestamp, int status, String message) {}
