package com.n26.controller;

import com.n26.model.Statistic;
import com.n26.model.Transaction;
import com.n26.service.StatisticService;
import com.n26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private StatisticService statisticsService;

    @GetMapping
    public ResponseEntity<Statistic> getStatistics() {

        List<Transaction> transactions = transactionService.findAllTransactions();
        Statistic statistics = statisticsService.create(transactions);

        return ResponseEntity.ok(statistics);
    }
}
