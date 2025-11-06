package com.property_broker.service;

import com.property_broker.entity.Transaction;
import java.util.List;

public interface TransactionService {
    List<Transaction> findAll();
    Transaction findById(String id);
    Transaction create(String offerId, Transaction transaction);
    void delete(String id);

}
