package com.example.personalfinances.repository;

import com.example.personalfinances.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTransactionId(String transactionId);

    Optional<Expense> findById(Long id);

    List<Expense> findByAccountNumber(String accountNumber);

    List<Expense> findByTransactionDataAndTransactionValue(String data, BigDecimal value);

}
