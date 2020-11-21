package com.n26.service;

import com.n26.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    private List<Transaction> transactions;

    public void createTransactionList() {
        if(transactions == null) {
            transactions = new ArrayList<>();
        }
    }

    public List<Transaction> getInDateTransactions() {
        createTransactionList();
        transactions.removeIf(x->(x.getTransactionDate().isBefore(ZonedDateTime.now().minusSeconds(60))));
        return transactions;
    }

    public void clearTransactions() {
        transactions.clear();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public boolean isTransactionInFuture(Transaction transaction) {
        return transaction.getTransactionDate().isAfter(ZonedDateTime.now());
    }

    public boolean isTransactionOlder(Transaction transaction) {
        ZonedDateTime transactionDate = transaction.getTransactionDate();
        if (transactionDate.isBefore(ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(60))) {
            return true;
        } else {
            return false;
        }
    }
}
