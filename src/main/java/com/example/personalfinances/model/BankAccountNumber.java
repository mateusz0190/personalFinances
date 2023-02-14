package com.example.personalfinances.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ban")
public class BankAccountNumber {
    @Id
    private String alias;
    private String name;
    private String number;

    private BigDecimal bilance;
    @OneToMany
    private Set<Expense> expenses;


}
