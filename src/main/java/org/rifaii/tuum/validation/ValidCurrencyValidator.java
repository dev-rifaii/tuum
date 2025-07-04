package org.rifaii.tuum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidCurrencyValidator implements ConstraintValidator<ValidCurrency, String> {

    //In a real world scenario, this can be replaced with a call to some external API providing valid currencies and cached
    public static final Set<String> VALID_CURRENCIES = Set.of("EUR", "SEK", "GBP", "USD");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (VALID_CURRENCIES.contains(value.toUpperCase()))
            return true;

        context.disableDefaultConstraintViolation();
        String errorMessage = "Currency '%s' is not allowed".formatted(value);
        context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
        return VALID_CURRENCIES.contains(value.toUpperCase());
    }
}
