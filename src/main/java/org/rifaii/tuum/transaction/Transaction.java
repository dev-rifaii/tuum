package org.rifaii.tuum.transaction;

import java.math.BigDecimal;
import java.util.Objects;

public class Transaction {

    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private String currencyCode;
    private Direction direction;
    private String description;

    public Transaction() {}

    public Transaction(Long accountId, BigDecimal amount, String currencyCode, Direction direction, String description) {
        this.accountId = accountId;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.direction = direction;
        this.description = description;
    }

    public enum Direction {
        IN, OUT
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && Objects.equals(accountId, that.accountId) && Objects.equals(
            amount,
            that.amount
        ) && Objects.equals(currencyCode, that.currencyCode) && direction == that.direction && Objects.equals(
            description,
            that.description
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountId, amount, currencyCode, direction, description);
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "id=" + id +
               ", accountId=" + accountId +
               ", amount=" + amount +
               ", currencyCode='" + currencyCode + '\'' +
               ", direction=" + direction +
               ", description='" + description + '\'' +
               '}';
    }
}
