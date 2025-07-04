package org.rifaii.tuum.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.rifaii.tuum.validation.EnumValue;
import org.rifaii.tuum.validation.ValidCurrency;
import org.rifaii.tuum.transaction.Transaction;
import org.rifaii.tuum.transaction.Transaction.Direction;

import java.math.BigDecimal;

public record CreateTransactionRequestDto(
    @NotNull Long accountId,
    @NotNull @Positive BigDecimal amount,
    @ValidCurrency @NotNull String currency,
    @NotNull @EnumValue(Direction.class) String direction,
    @NotBlank String description
) {

    public Transaction mapToTransaction() {
        return new Transaction(accountId, amount, currency, Direction.valueOf(direction), description);
    }
}
