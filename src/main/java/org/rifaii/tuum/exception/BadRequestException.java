package org.rifaii.tuum.exception;

public class BadRequestException extends RuntimeException implements CodedValue {

    public final String code;

    public BadRequestException(String message, String code) {
        super(message);
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
