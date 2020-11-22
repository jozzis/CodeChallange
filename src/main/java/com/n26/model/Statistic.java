package com.n26.model;

import java.math.BigDecimal;

public class Statistic {

    private String avg;
    private String sum;
    private String max;
    private String min;
    private long count;

    public void setCount(long count) {
        this.count = count;
    }
    public long getCount() {
        return this.count;
    }

    public void setAvg(BigDecimal avg) {
        this.avg = avg.toString();
    }
    public String getAvg() {
        return this.avg = avg;
    }

    public void setMin(BigDecimal min) {
        this.min = min.toString();
    }
    public String getMin() {
        return this.min = min;
    }

    public void setMax(BigDecimal max) {
        this.max = max.toString();
    }
    public String getMax() {
        return this.max = max;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum.toString();
    }
    public String getSum() {
        return this.sum = sum;
    }
}
