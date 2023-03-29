package com.example.personalfinances.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "expense")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String transactionData;
    private String transactionId;
    private String transactionTitle;
    private String transactionDescription;
    private BigDecimal transactionValue;

    private String bankName;
    private String accountNumber;

    private String comment;
}
