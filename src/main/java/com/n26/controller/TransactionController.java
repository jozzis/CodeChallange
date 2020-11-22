package com.n26.controller;

import com.n26.model.Transaction;
import com.n26.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private TransactionService transactionService;

    @PostMapping (consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity add(@RequestBody Transaction transaction) {
        try {
            HashMap isJSONValidHash = transactionService.handleJSON(transaction);
            if(isJSONValidHash.get("hasError") != (Boolean)true){
                transactionService.addTransaction(transaction);
            }
            return ResponseEntity.status(HttpStatus.valueOf((Integer) isJSONValidHash.get("httpStatus")))
                        .body((String) isJSONValidHash.get("message"));
            }catch(Exception e){
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
