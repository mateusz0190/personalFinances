package com.example.personalfinances.repository;

import com.example.personalfinances.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTransactionId(String transactionId);

    List<Expense> findByBankName(String bankName);

    List<Expense> findByAccountNumber(String accountNumber);

}
