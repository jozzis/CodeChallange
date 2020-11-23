package com.n26.service;

import com.n26.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Service that implements methods related to a transaction.
 */
@Service
public class TransactionService {
    private List<Transaction> transactions;

    /**
     * Method to create the transaction list
     *
     * Note: this is a synchronized singleton method to guarantee that
     *       when concurrent transaction requests happened, one
     *       and only one List<Transacrion> object is created.
     */
    public synchronized void createTransactionList() {
        if(transactions == null) {
            transactions = new ArrayList<>();
        }
    }

    /**
     * Method that checks if JSON a valid transaction.
     *
     * @param Transaction
     * @return HashMap<>
     */
    public HashMap checkJSON(Transaction transaction){
        try{
            /* try to parse JSON fields */
            BigDecimal amount = new BigDecimal(transaction.getAmount());
            ZonedDateTime timestamp = ZonedDateTime.parse(transaction.getTimestamp());

            /* check if transaction is in future */
            if (timestamp.isAfter(ZonedDateTime.now())){
                return fillJSONValidationMap(true
                        ,"The date introduced is in the future."
                        ,422 );

            /* check if transaction is older than 60sec */
            }else if(timestamp.isBefore(ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(60))) {
                return fillJSONValidationMap(true
                        ,"This transaction is older than 60sec. It will no longer be considered for statistics."
                        ,204 );
            }

        /* JSON fields are not parsable */
        } catch (Exception e) {
            return fillJSONValidationMap(true
                                ,"The fields should be parsable to following values:\n" +
                                                    " amount: BigDecimal\n" +
                                                    " timeStamp format: YYYY-MM-DD'T'hh:mm:ss.sssZ"
                                ,422 );
        }

        /* JSON are parsable and transaction is OK */
        return fillJSONValidationMap(false,"Transaction Created with Success.", 201);
    }

    /**
     * Method that fills the HasMap.
     *
     * @param boolean, string, integer
     * @return HashMap<>
     */
    private HashMap fillJSONValidationMap(Boolean hasError, String message, Integer httpStatus){
        HashMap<String,Object> jsonValidationMap = new HashMap<>();
        jsonValidationMap.put("hasError", hasError);
        jsonValidationMap.put("message", message);
        jsonValidationMap.put("httpStatus", httpStatus);
        return jsonValidationMap;
    }

    /**
     * Method to remove all transactions older than 60sec from transactions list.
     *
     * @return List<Transaction>
     */
    public List<Transaction> getInDateTransactions() {
        createTransactionList(); //makes sure that the transactions List exists before remove elements
        // lambda expression
        transactions.removeIf(x->ZonedDateTime.parse(x.getTimestamp())
                                .isBefore(ZonedDateTime.now().minusSeconds(60)));
        return transactions;
    }

    /**
     * Method to clear all transactions from transactions list.
     */
    public void clearTransactions() {
        createTransactionList(); //makes sure that the transaction List exists before clear.
        transactions.clear();
    }

    /**
     * Method to add a transaction to the transactions list.
     */
    public void addTransaction(Transaction transaction) {
        getInDateTransactions(); //makes sure that only valid transactions are present in transactions lists
        transactions.add(transaction);
    }

    /**
     * Method to clean objects created.
     */
    public void clearObjects() {
        transactions = null;
    }
}
