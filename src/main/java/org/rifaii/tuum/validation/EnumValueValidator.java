package org.rifaii.tuum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

    private Class<? extends Enum<?>> enumClass;
    private String errorMessage;

    @Override
    public void initialize(EnumValue annotation) {
        this.enumClass = annotation.value();
        String validValues = Arrays.stream(annotation.value().getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.joining(","));
        this.errorMessage = "Value must be one of %s".formatted(validValues);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
            .anyMatch(e -> e.name().equalsIgnoreCase(value));
        if (isValid)
            return true;

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
        return false;
    }
}
