package com.dockerino.demo.exception;

import com.dockerino.demo.exception.authentication.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<String> handle(AuthenticationException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<String> handle(ResourceNotFoundException resourceNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resourceNotFoundException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Error");

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        body.put("fields", fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handle(RuntimeException exception) {
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }

}
