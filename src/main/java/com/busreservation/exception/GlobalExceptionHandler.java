package com.busreservation.exception;

import com.busreservation.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for centralized error handling
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        log.error("IllegalArgumentException: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .statusCode(400)
                .errorCode("ILLEGAL_ARGUMENT")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {
        log.error("RuntimeException: {}", ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message("An error occurred: " + ex.getMessage())
                .data(null)
                .statusCode(500)
                .errorCode("RUNTIME_ERROR")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle generic Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex,
            WebRequest request) {
        log.error("Exception: {}", ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message("An unexpected error occurred: " + ex.getMessage())
                .data(null)
                .statusCode(500)
                .errorCode("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
