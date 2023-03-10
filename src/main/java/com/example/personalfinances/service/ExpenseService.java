package com.example.personalfinances.service;

import com.example.personalfinances.model.Expense;
import com.example.personalfinances.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;
import java.util.List;

@AllArgsConstructor
@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getAll() {
        return expenseRepository.findAll();
    }

    public List<Expense> getByTransactionId(String transactionId) {
        return expenseRepository.findByTransactionId(transactionId);
    }

    public List<Expense> getExpensesInMonth(String date) {
        List<Expense> expenses = getAll().stream().filter(expense -> {
            LocalDate expenseDateTime = parseToDate(expense.getTransactionData());
            LocalDate requestDate = parseToDate(date);
            return (requestDate.getYear() == expenseDateTime.getYear()
                    & requestDate.getMonth() == expenseDateTime.getMonth());
        }).toList();
        return expenses;
    }

    private LocalDate parseToDate(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (data.length() <= 6) {
            formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            data = data + "01";
        }
        LocalDate parse = LocalDate.parse(data, formatter);
        return parse;
    }
}
