package com.n26.model;

import java.math.BigDecimal;

public class Transaction {

    private String timestamp;
    private String amount;

    public String getAmount() {
        return this.amount;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public BigDecimal parseAmount(String amount){
        return new BigDecimal(amount);
    }
}