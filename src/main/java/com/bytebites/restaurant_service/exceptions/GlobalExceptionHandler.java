package com.bytebites.restaurant_service.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        StringBuilder errorMessageBuilder = new StringBuilder("Validation failed: ");

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessageBuilder
                    .append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        });

        String errorMessage = errorMessageBuilder.toString().replaceAll(";\\s*$", "");

        return new ResponseEntity<>(
                new ApiErrorResponse(LocalDateTime.now(),
                        errorMessage,
                        HttpStatus.UNPROCESSABLE_ENTITY.value()
                ), HttpStatus.UNPROCESSABLE_ENTITY);
    }



    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFound ex, WebRequest request) {
        return new ResponseEntity<>(
                new ApiErrorResponse(LocalDateTime.now(),
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                new ApiErrorResponse(LocalDateTime.now(),
                        ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Access Denied: You don't have the required permissions.");
    }
}
