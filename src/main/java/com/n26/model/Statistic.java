package com.n26.model;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Statistic {

    private BigDecimal avg;
    private BigDecimal sum;
    private BigDecimal max;
    private BigDecimal min;
    private long count;

    public void setCount(long count) {
        this.count = count;
    }

    public void setAvg(BigDecimal avg) {
        this.avg = avg;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
