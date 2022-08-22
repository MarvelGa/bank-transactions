package org.banktransaction.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Transaction {
    public enum TransactionType {
        CARD("card"), DIRECT_DEBIT("direct debit"), INTERNET("internet");

        TransactionType(String type) {
            this.type = type;
        }

        public final String type;
    }

    private LocalDate date;
    private String vendor;
    private TransactionType type;
    private BigDecimal amount;
    private String category;
}
