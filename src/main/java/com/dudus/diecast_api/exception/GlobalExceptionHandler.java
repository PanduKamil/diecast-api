package com.dudus.diecast_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //Kalo handle kgk ketemu
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex){
        Map<String, Object> error = new HashMap<>();
        error.put("status",  404);
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    //kalo gagal input
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex){
        Map<String, Object> error = new HashMap<>();
        error.put("status",  400);
        error.put("message", "Validasi gagal");
        error.put("timestamp", LocalDateTime.now());

        //kumpulan field2 error.
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(e -> fields.put(e.getField(), e.getDefaultMessage()));
        error.put("errors", fields);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
