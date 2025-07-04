package org.rifaii.tuum.account.dto;

import java.math.BigDecimal;
import java.util.List;

/*
 * Has to be a class instead of a record
 * due to mybatis limitation https://github.com/mybatis/mybatis-3/issues/3021#issuecomment-1833020059
 */
public class AccountWithBalanceDto {

    private Long accountId;
    private Long customerId;
    private List<BalanceDto> balances;

    public AccountWithBalanceDto() { }

    public Long getAccountId() {
        return accountId;
    }

    public AccountWithBalanceDto setAccountId(Long accountId) {
        this.accountId = accountId;
        return this;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public AccountWithBalanceDto setCustomerId(Long customerId) {
        this.customerId = customerId;
        return this;
    }

    public List<BalanceDto> getBalances() {
        return balances;
    }

    public AccountWithBalanceDto setBalances(List<BalanceDto> balances) {
        this.balances = balances;
        return this;
    }

    public static class BalanceDto {

        private BigDecimal availableAmount;
        private String currencyCode;

        public BalanceDto() {}

        public BigDecimal getAvailableAmount() {
            return availableAmount;
        }

        public BalanceDto setAvailableAmount(BigDecimal availableAmount) {
            this.availableAmount = availableAmount;
            return this;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public BalanceDto setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }
    }
}
