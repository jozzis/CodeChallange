package com.n26.service;

import com.n26.model.Statistic;
import com.n26.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class StatisticService {

    public Statistic create(List<Transaction> transactions) {

        Statistic statistics = new Statistic();

        statistics.setCount(transactions.stream().count());
        statistics.setAvg(BigDecimal.valueOf(transactions.stream()
                .mapToDouble(t -> t.parseAmount(t.getAmount()).doubleValue()).average().orElse(0.00))
                .setScale(2, RoundingMode.HALF_UP));
        statistics.setSum(BigDecimal.valueOf(transactions.stream()
                .mapToDouble(t -> t.parseAmount(t.getAmount()).doubleValue()).sum())
                .setScale(2, RoundingMode.HALF_UP));
        statistics.setMin(BigDecimal.valueOf(transactions.stream()
                .mapToDouble(t -> t.parseAmount(t.getAmount()).doubleValue()).min().orElse(0.00))
                .setScale(2, RoundingMode.HALF_UP));
        statistics.setMax(BigDecimal.valueOf(transactions.stream()
                .mapToDouble(t -> t.parseAmount(t.getAmount()).doubleValue()).max().orElse(0.00))
                .setScale(2, RoundingMode.HALF_UP));

        return statistics;
    }

}
