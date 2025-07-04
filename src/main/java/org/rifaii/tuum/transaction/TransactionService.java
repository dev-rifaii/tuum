package org.rifaii.tuum.transaction;

import org.rifaii.tuum.account.AccountService;
import org.rifaii.tuum.account.dto.AccountWithBalanceDto;
import org.rifaii.tuum.account.dto.AccountWithBalanceDto.BalanceDto;
import org.rifaii.tuum.balance.BalanceService;
import org.rifaii.tuum.exception.BusinessAssert;
import org.rifaii.tuum.exception.ResourceNotFoundException;
import org.rifaii.tuum.transaction.Transaction.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Transactional
@Service
public class TransactionService {

    private final TransactionDao transactionDao;
    private final BalanceService balanceService;
    private final AccountService accountService;

    TransactionService(TransactionDao transactionDao, BalanceService balanceService, AccountService accountService) {
        this.transactionDao = transactionDao;
        this.balanceService = balanceService;
        this.accountService = accountService;
    }

    void create(Transaction transaction) {
        validate(transaction);
        transactionDao.insert(transaction);
        balanceService.updateBalance(
            transaction.getAccountId(),
            transaction.getCurrencyCode(),
            transaction.getDirection() == Direction.IN ? transaction.getAmount() : transaction.getAmount().negate()
        );
    }

    List<Transaction> find(Long accountId) {
        accountService.getAccount(accountId);
        return transactionDao.find(accountId);
    }

    private void validate(Transaction transaction) {
        Long accountId = transaction.getAccountId();
        BigDecimal amount = transaction.getAmount();
        String currencyCode = transaction.getCurrencyCode();

        AccountWithBalanceDto account = accountService.getAccount(accountId);

        BalanceDto balanceForCurrency = account.getBalances()
            .stream()
            .filter(balance -> currencyCode.equals(balance.getCurrencyCode()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Currency %s not available on account".formatted(accountId)));

        if (Direction.IN.equals(transaction.getDirection()))
            return;

        BusinessAssert.isTrue(
            balanceForCurrency.getAvailableAmount().compareTo(amount) >= 0,
            "Insufficient funds",
            "INSUFFICIENT_FUNDS"
        );
    }
}
