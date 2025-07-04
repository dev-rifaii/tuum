package org.rifaii.tuum.balance;

import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
interface BalanceDao {

    void insert(List<Balance> balances);

    void update(Long accountId, String currencyCode, BigDecimal change);

    Optional<Balance> find(Long accountId, String currencyCode);
}
