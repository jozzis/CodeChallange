package com.n26.service;

import com.n26.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TransactionService {
    private List<Transaction> transactions;

    public synchronized void createTransactionList() {
        if(transactions == null) {
            transactions = new ArrayList<>();
        }
    }

    public HashMap handleJSON(Transaction transaction){
        try{
            BigDecimal amount = new BigDecimal(transaction.getAmount());
            ZonedDateTime timestamp = ZonedDateTime.parse(transaction.getTimestamp());
            if (timestamp.isAfter(ZonedDateTime.now())){
                return fillJSONValidationMap(true
                        ,"The date introduced is in the future."
                        ,422 );
            }else if(timestamp.isBefore(ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(60))) {
                return fillJSONValidationMap(true
                        ,"This transaction is older than 60sec. It will no longer be considered for statistics."
                        ,204 );
            }
        } catch (Exception e) {
            return fillJSONValidationMap(true
                                ,"The fields should be parsable to following values:\n" +
                                                    " amount: BigDecimal\n" +
                                                    " timeStamp format: YYYY-MM-DD'T'hh:mm:ss.sssZ"
                                ,422 );
        }
        return fillJSONValidationMap(false,"Transaction Created with Success.", 201);
    }

    private HashMap fillJSONValidationMap(Boolean hasError, String message, Integer httpStatus){
        HashMap<String,Object> jsonValidationMap = new HashMap<String, Object>();
        jsonValidationMap.put("hasError", hasError);
        jsonValidationMap.put("message", message);
        jsonValidationMap.put("httpStatus", httpStatus);
        return jsonValidationMap;
    }

    public List<Transaction> getInDateTransactions() {
        createTransactionList();
        transactions.removeIf(x->ZonedDateTime.parse(x.getTimestamp()).isBefore(ZonedDateTime.now().minusSeconds(60)));
        return transactions;
    }

    public void clearTransactions() {
        createTransactionList();
        transactions.clear();
    }

    public void addTransaction(Transaction transaction) {
        getInDateTransactions();
        transactions.add(transaction);
    }

    public void clearObjects() {
        transactions = null;
    }
}
