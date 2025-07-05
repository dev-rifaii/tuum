package org.rifaii.tuum.audit;

import org.junit.jupiter.api.Test;
import org.rifaii.tuum.ITestBase;
import org.rifaii.tuum.account.Account;
import org.rifaii.tuum.account.AccountDao;
import org.rifaii.tuum.balance.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class DataChangeStatementInterceptorTest extends ITestBase {

    @Autowired AccountDao accountDao;
    @Autowired BalanceService balanceService;

    @Test
    void intercept_PublishesDataChangesToRabbit_IfOperationIsInsert() {
        var input = new Account(141L, "EE");
        accountDao.insert(input);
        var changeLog = new DataChangeLog("INSERT", "org.rifaii.tuum.account.AccountDao.insert", input.toString());
        verify(dataChangeNotifier, times(1)).notify(eq(changeLog));
    }

    @Sql("classpath:sql/account_with_balance.sql")
    @Test
    void intercept_PublishesDataChangesToRabbit_IfOperationIsUpdate() {
        Long accountId = 51231L;
        balanceService.updateBalance(accountId, "EUR", BigDecimal.ONE);
        verify(dataChangeNotifier, times(1)).notify(any(DataChangeLog.class));
    }

    @Test
    void intercept_DoesNotPublishAnythingToRabbit_IfOperationIsSelect() {
        accountDao.find(4124123L);
        verifyNoInteractions(dataChangeNotifier);
    }

}