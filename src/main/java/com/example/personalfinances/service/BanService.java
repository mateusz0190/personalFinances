package com.example.personalfinances.service;

import com.example.personalfinances.model.BankAccountNumber;
import com.example.personalfinances.model.Expense;
import com.example.personalfinances.repository.BankAccountNumberRepository;
import com.example.personalfinances.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@AllArgsConstructor
@Service
public class BanService {
    private BankAccountNumberRepository banRepository;
    private ExpenseRepository expenseRepository;

    public BankAccountNumber createAccount(BankAccountNumber ban) {
        String name = ban.getName();
        ban.setAlias(name.replaceAll(" ", ""));
        ban.setBilance(BigDecimal.ZERO);
        return banRepository.save(ban);
    }

    public List<BankAccountNumber> getAll() {
        return banRepository.findAll();
    }

    public BankAccountNumber getByAccountName(String accountName) {
        return banRepository.findByName(accountName).get(0);
    }

    public BankAccountNumber assingExpense(String banId) {
        BankAccountNumber bankAccountNumber = banRepository.findById(banId).get();
        String name = "\"" + bankAccountNumber.getName() + "\"";
        List<Expense> expenses = expenseRepository.findByAccountNumber(name);
        bankAccountNumber.setExpenses(expenses);
        banRepository.save(bankAccountNumber);
        return bankAccountNumber;
    }

    public List<BankAccountNumber> getAllassignedtoBankAccountNumber() {
        List<BankAccountNumber> bankAccountNumbers = banRepository.findAll()
                .stream()
                .filter(bankAccountNumber -> bankAccountNumber.getExpenses().size() > 0)
                .toList();
        return bankAccountNumbers;
    }


    public void releaseAssignedExpenses(String accountName) {
        BankAccountNumber bankAccountNumber = getByAccountName(accountName);
        bankAccountNumber.setExpenses(new ArrayList<>());
        banRepository.save(bankAccountNumber);
    }

    public void assingExpenseByBanName(Expense expense) {
        BankAccountNumber bankAccountNumber = getByAccountName(expense.getAccountNumber());
        List<Expense> expenses = bankAccountNumber.getExpenses();
        expenses.add(expense);

        BigDecimal bigDecimal = updateBilance(bankAccountNumber, expense.getTransactionValue());
        bankAccountNumber.setExpenses(expenses);
        bankAccountNumber.setBilance(bigDecimal);
        banRepository.save(bankAccountNumber);
    }

    public BigDecimal updateBilance(BankAccountNumber bankAccountNumber, BigDecimal addedValue) {
        BigDecimal bilance = bankAccountNumber.getBilance();
        bilance = bilance.add(addedValue);

        return bilance;
    }
}
