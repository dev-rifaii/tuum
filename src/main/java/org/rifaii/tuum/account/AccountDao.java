package org.rifaii.tuum.account;

import org.apache.ibatis.annotations.Mapper;
import org.rifaii.tuum.account.dto.AccountWithBalanceDto;

import java.util.Optional;

@Mapper
public interface AccountDao {

    Optional<Account> find(Long id);

    void insert(Account account);

    Optional<AccountWithBalanceDto> findWithBalance(Long accountId);

}
