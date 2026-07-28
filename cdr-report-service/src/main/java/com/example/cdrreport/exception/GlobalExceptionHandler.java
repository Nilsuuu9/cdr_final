package com.example.cdrreport.exception;

import com.example.cdrreport.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            InvalidRequestException.class,
            ConstraintViolationException.class,
            HandlerMethodValidationException.class
    })
    public ResponseEntity<ApiError> handleValidationException(
            Exception exception,
            HttpServletRequest request) {
        log.error("Database request failed. path={}", request.getRequestURI(), exception);
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDatabaseException(
            DataAccessException exception,
            HttpServletRequest request) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Database error",
                "The request could not be completed because of a database error.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {
        log.error("Unexpected request failure. path={}", request.getRequestURI(), exception);
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "An unexpected error occurred.",
                request
        );
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(apiError);
    }
}
