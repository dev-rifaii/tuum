package org.rifaii.tuum.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    Instant timestamp,
    String errorId,
    List<Error> errors
) {
    public record Error(String code, String message){}
}
