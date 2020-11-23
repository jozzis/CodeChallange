package com.n26.service;

import com.n26.model.Statistic;
import com.n26.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Service that implements methods related to statistics.
 */

@Service
public class StatisticService {

    /**
     * Method that creates statistics based on the transactions.
     *
     * @param transactions
     * @return Statistic
     */
    public Statistic createStatistic(List<Transaction> transactions) {

        Statistic statistics = new Statistic();

        /* lambda expressions to get avg,sum,max,min,count from all existing objects in transactions
            and setting these values to statistics attributes */
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
