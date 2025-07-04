package org.rifaii.tuum.exception;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ErrorResponse {

    private final Instant timestamp;
    private final String errorId;
    private List<Error> errors;
    private List<InputFieldError> fieldErrors;

    public static ErrorResponse forFieldErrors(InputFieldError... fieldErrors) {
        var errorResponse = new ErrorResponse(Instant.now(), UUID.randomUUID().toString());
        errorResponse.fieldErrors = Arrays.stream(fieldErrors).toList();
        return errorResponse;
    }

    public static ErrorResponse forGeneralErrors(Error... errors) {
        var errorResponse = new ErrorResponse(Instant.now(), UUID.randomUUID().toString());
        errorResponse.errors = Arrays.stream(errors).toList();
        return errorResponse;
    }


    private ErrorResponse(Instant timestamp, String errorId) {
        this.timestamp = timestamp;
        this.errorId = errorId;
    }

    public String getErrorId() {
        return errorId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public List<InputFieldError> getFieldErrors() {
        return fieldErrors;
    }

    public record Error(String code, String message) {}

    public record InputFieldError(String field, String reasonCode, String message) {}
}
