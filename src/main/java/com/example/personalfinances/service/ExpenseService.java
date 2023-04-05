package com.example.personalfinances.service;

import com.example.personalfinances.model.BankAccountNumber;
import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Expense;
import com.example.personalfinances.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

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
        banService.assingExpenseByBanName(expense1);
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

    public List<Expense> getAllExpensesInMonth(String date) {
        return expensesInMonth(getAll().stream(), date);
    }

    public List<Expense> expensesInMonth(Stream<Expense> expenseStream, String date) {
        List<Expense> expenses = expenseStream.filter(expense -> {
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

    public void getExpensesWithoutCategory() {

    }

    public Map<String, BigDecimal> sumExpensesByCategoryInMonth(String yearMonth) {
        LocalDate reportDate = parseToDate(yearMonth);
        Map<String, BigDecimal> categoryMap = new HashMap<>();
        categoryService.getAll().stream().forEach(category -> {

            List<Expense> expenses = expensesByCategoryInMonth(category, yearMonth);
            if (expenses.size() > 0) {
                BigDecimal sumValueOfExpenses = sumValueOfExpenses(expenses);
                categoryMap.put(category.getName(), sumValueOfExpenses);
                System.out.println("categoryMap.size() = " + categoryMap.size());
            }

        });
        return categoryMap;
    }

    public List<Expense> expensesByCategoryInMonth(Category category, String yearMM) {
        return expensesInMonth(category.getExpenseList().stream(), yearMM);
    }

    public BigDecimal sumValueOfExpenses(List<Expense> expenses) {
        BigDecimal sum = BigDecimal.valueOf(0.0);
        for (Expense expense :
                expenses) {
            BigDecimal transactionValue = expense.getTransactionValue();
            sum = sum.add(transactionValue);
        }
        return sum;
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
