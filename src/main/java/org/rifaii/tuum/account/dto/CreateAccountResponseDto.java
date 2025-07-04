package org.rifaii.tuum.account.dto;

import java.math.BigDecimal;
import java.util.List;

public record CreateAccountResponseDto(
    Long accountId,
    Long customerId,
    List<BalanceDto> balances
) {
    public record BalanceDto(
        BigDecimal availableAmount,
        String currencyCode
    ){}
}
