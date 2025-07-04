package org.rifaii.tuum.account;

import jakarta.validation.Valid;
import org.rifaii.tuum.account.dto.AccountWithBalanceDto;
import org.rifaii.tuum.account.dto.CreateAccountRequestDto;
import org.rifaii.tuum.account.dto.CreateAccountResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/accounts")
@RestController
class AccountApi {

    private final AccountService accountService;

    AccountApi(AccountService accountService) {
        this.accountService = accountService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    CreateAccountResponseDto create(@RequestBody @Valid CreateAccountRequestDto input) {
        return accountService.createAccount(input);
    }

    @GetMapping("/{accountId}")
    AccountWithBalanceDto get(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

}
