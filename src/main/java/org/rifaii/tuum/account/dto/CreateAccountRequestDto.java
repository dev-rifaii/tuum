package org.rifaii.tuum.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.rifaii.tuum.validation.ValidCurrency;

import java.util.List;

public record CreateAccountRequestDto(
    @Positive @NotNull Long customerId,
    @NotBlank @Size(max = 2, min = 2) String countryCode,
    List<@ValidCurrency @NotBlank String> currencies
) {
}
