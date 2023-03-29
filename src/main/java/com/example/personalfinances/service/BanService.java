package com.example.personalfinances.service;

import com.example.personalfinances.model.BankAccountNumber;
import com.example.personalfinances.model.Expense;
import com.example.personalfinances.repository.BankAccountNumberRepository;
import com.example.personalfinances.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class BanService {
    private BankAccountNumberRepository banRepository;
    private ExpenseRepository expenseRepository;

    public BankAccountNumber createAccount(BankAccountNumber ban) {
        String name = ban.getName();
        ban.setAlias(name.replaceAll(" ", ""));
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
        List<Expense> expenses = expenseRepository.findByBankName(name);
        expenses = expenseRepository.findByAccountNumber(name);
        Set<Expense> set = new HashSet<>(expenses);

        bankAccountNumber.setExpenses(set);
        banRepository.save(bankAccountNumber);
        return bankAccountNumber;
    }

    public List<BankAccountNumber> getAllassignedtoBankAccountNumber(){
        List<BankAccountNumber> bankAccountNumbers = banRepository.findAll()
                .stream()
                .filter(bankAccountNumber -> bankAccountNumber.getExpenses().size() > 0)
                .toList();
        return bankAccountNumbers;
    }


    public void releaseAssignedExpenses(String accountName) {
        BankAccountNumber bankAccountNumber = getByAccountName(accountName);
        bankAccountNumber.setExpenses(new HashSet<>());
        banRepository.save(bankAccountNumber);
    }

    public void assingExpenses(Expense expense) {

    }
}
