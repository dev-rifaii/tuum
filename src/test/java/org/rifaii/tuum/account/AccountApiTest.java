package org.rifaii.tuum.account;

import org.junit.jupiter.api.Test;
import org.rifaii.tuum.ITestBase;
import org.rifaii.tuum.account.dto.CreateAccountRequestDto;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountApiTest extends ITestBase {

    @Test
    void create_Returns201AndCreatesAccount_GivenValidInput() throws Exception {
        var input = new CreateAccountRequestDto(5L, "EE", List.of("EUR", "USD"));

        mockMvc
            .perform(
                post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(input))
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accountId").isNumber())
            .andExpect(jsonPath("$.customerId", is(5)))
            .andExpect(jsonPath("$.balances").isArray())
            .andExpect(jsonPath("$.balances").isNotEmpty())
            .andExpect(jsonPath("$.balances[*].currencyCode", containsInAnyOrder("EUR", "USD")))
            .andExpect(jsonPath("$.balances[*].availableAmount", containsInAnyOrder(0, 0)))
        ;
    }

    @Test
    void create_Returns400_GivenInvalidCurrency() throws Exception {
        var input = new CreateAccountRequestDto(5L, "EE", List.of("EUR", "LBP"));
        mockMvc
            .perform(
                post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(input))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errorId").exists())
            .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
            .andExpect(jsonPath("$.fieldErrors[0].field", is("currencies[1]")))
            .andExpect(jsonPath("$.fieldErrors[0].reasonCode", is("ValidCurrency")))
            .andExpect(jsonPath("$.errors", nullValue()))
        ;
    }

    @Test
    void get_Returns404_IfAccountDoesNotExist() throws Exception {
        mockMvc
            .perform(get("/api/v1/accounts/51"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errorId").exists())
            .andExpect(jsonPath("$.fieldErrors", nullValue()))
            .andExpect(jsonPath("$.errors[0].message", nullValue()))
            .andExpect(jsonPath("$.errors[0].code", is("NOT_FOUND")))
        ;
    }

    @Sql("classpath:sql/account_with_multiple_balances.sql")
    @Test
    void get_ReturnsAccount_IfAccountExists() throws Exception {
        mockMvc
            .perform(get("/api/v1/accounts/51"))
            .andExpect(status().is(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accountId", is(51)))
            .andExpect(jsonPath("$.customerId", is(413)))
            .andExpect(jsonPath("$.balances", hasSize(2)))
            .andExpect(jsonPath("$.balances[*].currencyCode", containsInAnyOrder("SEK", "USD")))
            .andExpect(jsonPath("$.balances[*].availableAmount", containsInAnyOrder(0.0, 0.0)))
        ;
    }
}