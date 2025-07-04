package org.rifaii.tuum.transaction.dto;

import org.rifaii.tuum.transaction.Transaction;
import org.rifaii.tuum.transaction.Transaction.Direction;

import java.math.BigDecimal;

public record TransactionDto(
    Long transactionId,
    Long accountId,
    BigDecimal amount,
    String currency,
    Direction direction,
    String description
) {

    public static TransactionDto from(Transaction transaction) {
        return new TransactionDto(
            transaction.getId(),
            transaction.getAccountId(),
            transaction.getAmount(),
            transaction.getCurrencyCode(),
            transaction.getDirection(),
            transaction.getDescription()
        );
    }
}
