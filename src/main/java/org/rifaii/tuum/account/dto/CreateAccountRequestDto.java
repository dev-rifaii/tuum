package org.rifaii.tuum.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.rifaii.tuum.validation.ValidCurrency;

import java.util.List;

public record CreateAccountRequestDto(
    @NotNull Long customerId,
    @NotBlank String countryCode,
    List<@ValidCurrency @NotNull String> currencies
) {
}
