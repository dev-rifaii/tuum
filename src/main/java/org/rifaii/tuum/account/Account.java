package org.rifaii.tuum.account;

import java.util.Objects;

public class Account {

    private Long id;
    private Long customerId;
    private String countryCode;

    public Account(Long customerId, String countryCode) {
        this.customerId = customerId;
        this.countryCode = countryCode;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(customerId, account.customerId) && Objects.equals(
            countryCode,
            account.countryCode
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, countryCode);
    }

    @Override
    public String toString() {
        return "Account{" +
               "id=" + id +
               ", customerId=" + customerId +
               ", countryCode='" + countryCode + '\'' +
               '}';
    }
}
