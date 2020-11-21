package com.n26.controller;

import com.n26.model.Transaction;
import com.n26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    @ResponseBody
    public ResponseEntity add(@RequestBody Transaction transaction){
        try {
                if(transactionService.isTransactionInFuture(transaction)) {
                    return ResponseEntity.status(HttpStatus.valueOf(422))
                            .body("Transaction Date is in future.");
                } else {
                    if (transactionService.isTransactionOlder(transaction)) {
                        return ResponseEntity.status(HttpStatus.valueOf(204))
                                .body("More than 60 seconds have passed since this transaction. " +
                                        "For this reason it will not be saved.");
                    } else {
                        transactionService.createTransactionList();
                        transactionService.addTransaction(transaction);
                        return ResponseEntity.status(HttpStatus.valueOf(201))
                                .body(transactionService.findAllTransactions());
                    }
                }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete() {
        try {
            transactionService.clearTransactions();
            return ResponseEntity.status(HttpStatus.valueOf(204)).body(null);
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
