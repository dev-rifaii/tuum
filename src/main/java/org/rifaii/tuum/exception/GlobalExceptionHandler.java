package org.rifaii.tuum.exception;

import org.rifaii.tuum.exception.ErrorResponse.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

import static java.util.Arrays.asList;

@RestControllerAdvice
public class GlobalExceptionHandler {

    final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    ResponseEntity<ErrorResponse> general(Exception e) {
        ErrorResponse errorResponse = errorResponse();
        log.error("Error ID: {}, with message: {}", errorResponse.errorId(), e.getMessage(), e);
        return ResponseEntity.internalServerError()
            .body(errorResponse);
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e) {
        Error[] allFieldErrors = e.getAllErrors()
            .stream()
            .map(error -> new Error(error.getCode(), error.getDefaultMessage()))
            .toArray(Error[]::new);

        ErrorResponse errorResponse = errorResponse(allFieldErrors);
        log.error("Error ID: {}, with message {}", errorResponse.errorId(), e.getMessage(), e);
        return ResponseEntity.badRequest()
            .body(errorResponse);
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> resourceNotFound(ResourceNotFoundException e) {
        ErrorResponse errorResponse = errorResponse(new Error("RESOURCE_NOT_FOUND", e.getMessage()));
        log.error("Error ID: {}, with message {}", errorResponse.errorId(), e.getMessage(), e);
        return ResponseEntity.status(404)
            .body(errorResponse);
    }

    private ErrorResponse errorResponse(Error... errors) {
        return new ErrorResponse(
            Instant.now(),
            UUID.randomUUID().toString(),
            asList(errors)
        );
    }
}
