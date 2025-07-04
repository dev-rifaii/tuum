package org.rifaii.tuum.balance;

import org.junit.jupiter.api.Test;
import org.rifaii.tuum.ITestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BalanceServiceTest extends ITestBase {

    @Autowired BalanceService balanceService;

    @Sql("classpath:sql/account_with_balance.sql")
    @Test
    void change_DecreasesBalance_IfPassedChangeIsNegative() {
        Long accountId = 51231L;
        String currencyCode = "EUR";

        balanceService.updateBalance(accountId, currencyCode, BigDecimal.TEN.negate());

        Balance balanceAfterChange = balanceService.find(accountId, currencyCode).orElseThrow();

        assertEquals(new BigDecimal("45.21"), balanceAfterChange.getAmount());
    }

}