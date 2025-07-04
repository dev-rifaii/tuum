package org.rifaii.tuum.account;

import org.junit.jupiter.api.Test;
import org.rifaii.tuum.ITestBase;
import org.rifaii.tuum.account.dto.AccountWithBalanceDto;
import org.rifaii.tuum.account.dto.AccountWithBalanceDto.BalanceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountDaoTest extends ITestBase {

    @Autowired AccountDao accountDao;

    @Sql(statements = """
                      INSERT INTO account
                          (id, customer_id, country_code)
                      VALUES
                          (1, 1, 'EE');
                      
                      INSERT INTO balance
                          (account_id, amount, currency_code)
                      VALUES
                          (1, 50.00, 'EUR'),
                          (1, 1000.00, 'SEK'),
                          (1, 10.51, 'USD');
                      """)
    @Test
    void findWithBalance_ReturnsAccountWithAllBalances_IfMoreThanOnceBalanceExists() {
        Optional<AccountWithBalanceDto> accountWithBalances = accountDao.findWithBalance(1L);

        assertTrue(accountWithBalances.isPresent());
        var result = accountWithBalances.get();

        assertEquals(1, result.getAccountId());
        assertEquals(1, result.getCustomerId());
        assertEquals(3, result.getBalances().size());

        Function<String, BigDecimal> findBalanceForCurrency = (currencyCode) -> result.getBalances()
            .stream()
            .filter(balance -> currencyCode.equals(balance.getCurrencyCode()))
            .map(BalanceDto::getAvailableAmount)
            .findFirst()
            .orElseThrow();

        assertEquals(new BigDecimal("50.00"), findBalanceForCurrency.apply("EUR"));
        assertEquals(new BigDecimal("1000.00"), findBalanceForCurrency.apply("SEK"));
        assertEquals(new BigDecimal("10.51"), findBalanceForCurrency.apply("USD"));
    }

}