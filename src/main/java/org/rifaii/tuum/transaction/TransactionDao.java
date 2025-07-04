package org.rifaii.tuum.transaction;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
interface TransactionDao {

    void insert(Transaction transaction);

    List<Transaction> find(Long accountId);
}
