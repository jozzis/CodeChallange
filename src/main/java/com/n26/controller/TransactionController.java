package com.n26.controller;

import com.n26.model.Transaction;
import com.n26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * RestController that creates all service endpoints related to transactions.
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Method that creates a transaction.
     *
     * @param transaction,    amount    – transaction amount, a string of arbitrary length that is
     *                                    parsable as a BigDecimal;
     *                     timestamp    – transaction time in the ISO 8601 format
     *                                    YYYY-MM-DDThh:mm:ss.sssZ in the UTC timezone;
     *
     * @return ResponseEntity:
     *              201 – in case of success
     *              204 – if the transaction is older than 60 seconds
     *              400 – if the JSON is invalid
     *              422 – if any of the fields are not parsable or the transaction date is in the future
     */
    @PostMapping (consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity add(@Validated final @RequestBody Transaction transaction) {
        try {
            /* check if JSON is valid */
            HashMap isJSONValidHash = transactionService.checkJSON(transaction);

            /* add transaction if JSON is OK */
            if(isJSONValidHash.get("hasError") == (Boolean)false){
                transactionService.addTransaction(transaction);
            }

            /* return ResponseEntity accordingly to isJSONValid HashMap */
            return ResponseEntity.status(HttpStatus.valueOf((Integer) isJSONValidHash.get("httpStatus")))
                        .body((String) isJSONValidHash.get("message"));

            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while making transaction: " + e.getMessage());
            }
        }

    /**
     * Method that deletes all existing transactions.
     *
     * @return ResponseEntity (204)
     */
    @DeleteMapping
    public ResponseEntity delete() {
        try {
            transactionService.clearTransactions();
            return ResponseEntity.status(HttpStatus.valueOf(204)).body(null);
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
