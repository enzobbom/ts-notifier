package com.javanauta.ts.notifier.presentation.exception;

import com.javanauta.ts.notifier.application.exception.EmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.zone.ZoneRulesException;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // input validation exceptions

    // Handles validation errors from @Valid annotations in controller methods
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body("Validation failed: " + details);
    }

    // Handles JSON parsing errors, such as malformed JSON or type mismatches
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("Malformed JSON or invalid data type: " + ex.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(ZoneRulesException.class)
    public ResponseEntity<String> handleZoneRulesException(ZoneRulesException ex) {
        return ResponseEntity.badRequest().body("Invalid Time Zone ID: " + ex.getMessage());
    }

    // service exceptions

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<String> handlerEmailException(EmailException ex) {
        log.error("Email exception", ex);
        return ResponseEntity.internalServerError().body("Failed to send task notification email");
    }

    // Generic error handling

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.internalServerError().body("Internal server error");
    }
}
