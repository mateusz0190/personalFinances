package com.example.personalfinances.service;

import com.example.personalfinances.model.BankAccountNumber;
import com.example.personalfinances.model.Expense;
import com.example.personalfinances.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@AllArgsConstructor
@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;
    private BanService banService;
    private CategoryService categoryService;


    public Expense createExpense(Expense expense) {
        List<Expense> duplicates = getByTransactionId(expense.getTransactionId())
                .stream()
                .filter(expense1 -> expense1.getTransactionValue().equals(expense.getTransactionValue()))
                .toList();

        LinkedList<Expense> linkedExpenses = new LinkedList<>(duplicates);
        if (linkedExpenses.size() > 1) {
            return removeDuplicates(linkedExpenses);
        }

        Expense expense1 = duplicates.stream().findAny().orElseGet(
                () -> expenseRepository.save(expense));
        categoryService.assignExpenseToDefaultCategory(expense1);
        return expense1;
    }

    public List<Expense> getAll() {
        int size = expenseRepository.findAll().size();
        System.out.println("size = " + size);
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

    public Map<BankAccountNumber, List<Expense>> getAllAssignedExpensesToBan() {
        Map<BankAccountNumber, List<Expense>> map = new HashMap<>();
        List<BankAccountNumber> bankAccountNumbers = banService.getAllassignedtoBankAccountNumber();

        for (BankAccountNumber bankAccountNumber :
                bankAccountNumbers) {
            List<Expense> list = new ArrayList<>();
            bankAccountNumber.getExpenses().stream().forEach(expense -> list.add(expense));
            map.put(bankAccountNumber, list);
        }
        return map;
    }

    public void getExpensesWithoutCategory(){

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

    private Expense removeDuplicates(LinkedList<Expense> expenseLinkedList) {
        Expense expense = expenseLinkedList.removeFirst();
        expenseRepository.deleteAll(expenseLinkedList);
        return expense;
    }

    public void removeAll() {
        getAllAssignedExpensesToBan().keySet().forEach(ban -> banService.releaseAssignedExpenses(ban.getName()));
        expenseRepository.deleteAll(getAll());
    }
}
