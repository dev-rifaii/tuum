package org.rifaii.tuum.transaction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.rifaii.tuum.ITestBase;
import org.rifaii.tuum.balance.Balance;
import org.rifaii.tuum.balance.BalanceService;
import org.rifaii.tuum.transaction.Transaction.Direction;
import org.rifaii.tuum.transaction.dto.CreateTransactionRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionApiTest extends ITestBase {

    @Autowired BalanceService balanceService;

    @Sql("classpath:sql/account_with_balance.sql")
    @CsvSource({
        "OUT, 42.90",
        "IN, 67.52"
    })
    @ParameterizedTest
    void create_IncomingTransactionAddsMoneyToAccount_GivenValidInput(Direction direction, String expectedBalance) throws Exception {
        Long accountId = 51231L;

        var input = new CreateTransactionRequestDto(
            accountId,
            new BigDecimal("12.31"),
            "EUR",
            direction.name(),
            "testing"
        );

        mockMvc
            .perform(
                post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(input))
            )
            .andExpect(status().isCreated());

        Balance balanceAfterTransaction = balanceService.find(accountId, "EUR").get();
        assertEquals(new BigDecimal(expectedBalance), balanceAfterTransaction.getAmount());
    }

}