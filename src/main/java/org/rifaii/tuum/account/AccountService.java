package org.rifaii.tuum.account;

import org.rifaii.tuum.account.dto.AccountWithBalanceDto;
import org.rifaii.tuum.account.dto.CreateAccountRequestDto;
import org.rifaii.tuum.account.dto.CreateAccountResponseDto;
import org.rifaii.tuum.account.dto.CreateAccountResponseDto.BalanceDto;
import org.rifaii.tuum.balance.Balance;
import org.rifaii.tuum.balance.BalanceService;
import org.rifaii.tuum.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Transactional
@Service
public class AccountService {

    private final AccountDao accountDao;
    private final BalanceService balanceService;

    public AccountService(AccountDao accountDao, BalanceService balanceService) {
        this.accountDao = accountDao;
        this.balanceService = balanceService;
    }

    public CreateAccountResponseDto createAccount(CreateAccountRequestDto input) {
        var account = new Account(input.customerId(), input.countryCode());
        accountDao.insert(account);
        Long createdAccountId = account.getId();

        List<BalanceDto> balances = createBalances(createdAccountId, input.currencies());

        return new CreateAccountResponseDto(createdAccountId, input.customerId(), balances);
    }

    public AccountWithBalanceDto getAccount(Long accountId) {
        return accountDao.findWithBalance(accountId).orElseThrow(ResourceNotFoundException::new);
    }

    private List<BalanceDto> createBalances(Long accountId, Collection<String> currencies) {
        List<Balance> balances = currencies
            .stream()
            .map(currencyCode -> new Balance(accountId, BigDecimal.ZERO, currencyCode))
            .toList();

        balanceService.create(balances);

        return balances.stream()
            .map(balance -> new BalanceDto(balance.getAmount(), balance.getCurrencyCode()))
            .toList();
    }
}
