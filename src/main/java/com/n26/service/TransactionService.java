package com.n26.service;

import com.n26.model.Transaction;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        transactions.removeIf(x->(x.getTimestamp().isBefore(ZonedDateTime.now().minusSeconds(60))));
        return transactions;
    }

    public Transaction createTransaction(JSONObject jsonTransaction) throws JSONException {
        Transaction transaction = new Transaction();
        transaction.setAmount(jsonTransaction.get("amount") != null ? parseAmount(jsonTransaction) : transaction.getAmount());
        transaction.setTimestamp(jsonTransaction.get("timestamp") != null ?
                parseTimestamp(jsonTransaction) : transaction.getTimestamp());
        return transaction;
    }

    private BigDecimal parseAmount(JSONObject transaction) throws JSONException {
        return new BigDecimal((String) transaction.get("amount"));
    }

    private ZonedDateTime parseTimestamp(JSONObject transaction) throws JSONException {
        String timestamp = (String) transaction.get("timestamp");
        return ZonedDateTime.parse(timestamp);
    }

    public void clearTransactions() {
        createTransactionList();
        transactions.clear();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public boolean isTransactionInFuture(Transaction transaction) {
        return transaction.getTimestamp().isAfter(ZonedDateTime.now());
    }

    public boolean isTransactionOlder(Transaction transaction) {
        ZonedDateTime timestamp = transaction.getTimestamp();
        if (timestamp.isBefore(ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(60))) {
            return true;
        } else {
            return false;
        }
    }

    public void clearObjects() {
        transactions = null;
    }
}
