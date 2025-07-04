package org.rifaii.tuum.transaction;

import org.junit.jupiter.api.Test;
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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Test
    void create_Returns400_GivenInvalidInput() throws Exception {
        var input = new CreateTransactionRequestDto(
            null,
            null,
            null,
            null,
            null
        );

        mockMvc
            .perform(
                post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(input))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errorId").exists())
            .andExpect(jsonPath("$.errors", nullValue()))
            .andExpect(jsonPath("$.fieldErrors", hasSize(5)))
            .andExpect(jsonPath("$.fieldErrors[?(@.field=='amount')].reasonCode").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[?(@.field=='direction')].reasonCode").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[?(@.field=='description')].reasonCode").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[?(@.field=='accountId')].reasonCode").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[?(@.field=='currency')].reasonCode").value("NotNull"))
        ;
    }

    @Test
    void create_Returns404_IfAccountDoesNotExist() throws Exception {
        var input = new CreateTransactionRequestDto(
            1L,
            BigDecimal.TEN,
            "EUR",
            "IN",
           "Testing"
        );

        mockMvc
            .perform(
                post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(input))
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errorId").exists())
            .andExpect(jsonPath("$.fieldErrors", nullValue()))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", nullValue()))
            .andExpect(jsonPath("$.errors[0].code", is("NOT_FOUND")))
        ;
    }

    @Sql("classpath:sql/account_with_balance.sql")
    @Test
    void create_Returns400_IfAccountHasInsufficientFunds() throws Exception {
        var input = new CreateTransactionRequestDto(
            51231L,
            new BigDecimal("10000.00"),
            "EUR",
            "OUT",
            "Testing"
        );

        mockMvc
            .perform(
                post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(input))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errorId").exists())
            .andExpect(jsonPath("$.fieldErrors", nullValue()))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", is("Insufficient funds")))
            .andExpect(jsonPath("$.errors[0].code", is("INSUFFICIENT_FUNDS")))
        ;
    }

    @Sql("classpath:sql/account_with_balance.sql")
    @Test
    void create_Returns400_IfAccountDoesNotSupportProvidedCurrency() throws Exception {
        var input = new CreateTransactionRequestDto(
            51231L,
            new BigDecimal("1.00"),
            "USD",
            "OUT",
            "Testing"
        );

        mockMvc
            .perform(
                post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(input))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errorId").exists())
            .andExpect(jsonPath("$.fieldErrors", nullValue()))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", is("Currency USD not available on account")))
            .andExpect(jsonPath("$.errors[0].code", is("UNSUPPORTED_CURRENCY")))
        ;
    }

    @Test
    void create_Returns400_GivenNegativeAmount() throws Exception {
        var input = new CreateTransactionRequestDto(
            15L,
            new BigDecimal("-10.00"),
            "EUR",
            "IN",
            "Testing"
        );

        mockMvc
            .perform(
                post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(input))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errorId").exists())
            .andExpect(jsonPath("$.errors", nullValue()))
            .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
            .andExpect(jsonPath("$.fieldErrors[?(@.field=='amount')].reasonCode").value("Positive"))
        ;
    }

    @Test
    void create_Returns400_GivenInvalidDirection() throws Exception {
        var input = new CreateTransactionRequestDto(
            15L,
            new BigDecimal("10.00"),
            "EUR",
            "TESTTTT",
            "Testing"
        );

        mockMvc
            .perform(
                post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(input))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errorId").exists())
            .andExpect(jsonPath("$.errors", nullValue()))
            .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
            .andExpect(jsonPath("$.fieldErrors[?(@.field=='direction')].reasonCode").value("EnumValue"))
        ;
    }

    @Test
    void create_Returns400_GivenInvalidCurrency() throws Exception {
        var input = new CreateTransactionRequestDto(
            15L,
            new BigDecimal("10.00"),
            "SSS",
            "OUT",
            "Testing"
        );

        mockMvc
            .perform(
                post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(input))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errorId").exists())
            .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
            .andExpect(jsonPath("$.fieldErrors[0].field", is("currency")))
            .andExpect(jsonPath("$.fieldErrors[0].reasonCode", is("ValidCurrency")))
            .andExpect(jsonPath("$.errors", nullValue()))
        ;
    }

    @Test
    void find_Returns404_IfAccountDoesNotExist() throws Exception {
        mockMvc
            .perform(get("/api/v1/transactions/41"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errorId").exists())
            .andExpect(jsonPath("$.fieldErrors", nullValue()))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", nullValue()))
            .andExpect(jsonPath("$.errors[0].code", is("NOT_FOUND")))
        ;
    }

    @Sql("classpath:sql/account_with_transactions.sql")
    @Test
    void find_ReturnsTransactions_IfAnyExist() throws Exception {
        mockMvc
            .perform(get("/api/v1/transactions/8123"))
            .andExpect(status().is(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[*].amount", containsInAnyOrder(10.00, 10.00, 14.00, 0.21)))
            .andExpect(jsonPath("$[*].direction", containsInAnyOrder("IN", "OUT", "IN", "IN")))
            .andExpect(jsonPath("$[*].description", containsInAnyOrder("yes", "no", "yes", "yes")))
            .andExpect(jsonPath("$[*].currency", everyItem(is("EUR"))))
        ;
    }

}