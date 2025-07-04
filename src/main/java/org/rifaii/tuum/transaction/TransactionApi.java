package org.rifaii.tuum.transaction;

import jakarta.validation.Valid;
import org.rifaii.tuum.transaction.dto.CreateTransactionRequestDto;
import org.rifaii.tuum.transaction.dto.TransactionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/transactions")
@RestController
class TransactionApi {

    private final TransactionService transactionService;

    TransactionApi(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@RequestBody @Valid CreateTransactionRequestDto input) {
        transactionService.create(input.mapToTransaction());
    }

    @GetMapping("{accountId}")
    List<TransactionDto> find(@PathVariable Long accountId) {
        return transactionService.find(accountId)
            .stream()
            .map(TransactionDto::from)
            .toList();
    }
}
