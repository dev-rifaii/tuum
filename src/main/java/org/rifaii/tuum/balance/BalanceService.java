package org.rifaii.tuum.balance;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class BalanceService {

    private final BalanceDao balanceDao;

    BalanceService(BalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }

    public void create(List<Balance> balance) {
        balanceDao.insert(balance);
    }

    public void updateBalance(Long accountId, String currencyCode, BigDecimal change) {
        balanceDao.update(accountId, currencyCode, change);
    }

    public Optional<Balance> find(Long accountId, String currencyCode) {
       return balanceDao.find(accountId, currencyCode);
    }
}
