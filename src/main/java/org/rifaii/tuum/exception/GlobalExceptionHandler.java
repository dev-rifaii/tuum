package org.rifaii.tuum.exception;

import org.rifaii.tuum.exception.ErrorResponse.Error;
import org.rifaii.tuum.exception.ErrorResponse.InputFieldError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    ResponseEntity<ErrorResponse> general(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.forGeneralErrors();
        log.error("Error ID: {}, with message: {}", errorResponse.getErrorId(), e.getMessage(), e);
        return ResponseEntity.internalServerError()
            .body(errorResponse);
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e) {
        InputFieldError[] allFieldErrors = e.getFieldErrors()
            .stream()
            .map(error -> new InputFieldError(error.getField(), error.getCode(), error.getDefaultMessage()))
            .toArray(InputFieldError[]::new);

        ErrorResponse errorResponse = ErrorResponse.forFieldErrors(allFieldErrors);
        log.error("Error ID: {}, with message {}", errorResponse.getErrorId(), e.getMessage(), e);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> resourceNotFound(ResourceNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.forGeneralErrors(new Error(e.getCode(), e.getMessage()));
        log.error("Error ID: {}, with message {}", errorResponse.getErrorId(), e.getMessage(), e);
        return ResponseEntity.status(404)
            .body(errorResponse);
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> badRequest(BadRequestException e) {
        ErrorResponse errorResponse = ErrorResponse.forGeneralErrors(new Error(e.getCode(), e.getMessage()));
        log.error("Error ID: {}, with message {}", errorResponse.getErrorId(), e.getMessage(), e);
        return ResponseEntity.badRequest()
            .body(errorResponse);
    }
}
