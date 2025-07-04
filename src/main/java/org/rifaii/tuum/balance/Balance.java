package org.rifaii.tuum.balance;

import java.math.BigDecimal;
import java.util.Objects;

public class Balance {

    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private String currencyCode;

    public Balance() {}

    public Balance(Long accountId, BigDecimal amount, String currencyCode) {
        this.accountId = accountId;
        this.amount = amount;
        this.currencyCode = currencyCode;
    }

    public Long getId() {
        return id;
    }

    public Balance setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Balance setAccountId(Long accountId) {
        this.accountId = accountId;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Balance setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Balance setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Balance balance = (Balance) o;
        return Objects.equals(id, balance.id) && Objects.equals(accountId, balance.accountId) && Objects.equals(
            amount,
            balance.amount
        ) && Objects.equals(currencyCode, balance.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountId, amount, currencyCode);
    }

    @Override
    public String toString() {
        return "Balance{" +
               "id=" + id +
               ", accountId=" + accountId +
               ", amount=" + amount +
               ", currencyCode='" + currencyCode + '\'' +
               '}';
    }
}
