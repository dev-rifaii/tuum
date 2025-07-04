package org.rifaii.tuum.exception;

public class BusinessAssert {

    public static void isTrue(boolean expression, String message, String code) {
        if (expression)
            return;

        throw new BadRequestException(message, code);
    }
}
