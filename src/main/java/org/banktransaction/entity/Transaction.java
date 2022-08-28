package org.banktransaction.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    public enum TransactionType {
        CARD("card"), DIRECT_DEBIT("direct debit"), INTERNET("internet");

        TransactionType(String type) {
            this.type = type;
        }

        public final String type;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    private LocalDate date;
    private String vendor;
    private TransactionType type;
    private BigDecimal amount;
    private String category;
}
