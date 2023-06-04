package com.example.personalfinances.service;

import com.example.personalfinances.helper.CsvHelper;
import com.example.personalfinances.model.BankAccountNumber;
import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Expense;
import com.example.personalfinances.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;
    private BanService banService;
    private CategoryService categoryService;

    private Expense prepareToCreateExpense(Expense expense) {
        if (expense.getTransactionData().contains(".")) {
            LocalDate localDate = parseToDate(expense.getTransactionData());
            expense.setTransactionData(localDate.toString());
        }
        if (expense.getTransactionId().isEmpty()) {
            expense.setTransactionId("toCheckOrUpdate");
        }
        return expense;
    }

    public Expense createExpense(Expense expense) {
        expense = prepareToCreateExpense(expense);
        if (updateEmptyTransactionId(expense).isPresent()) {
            Expense expenseToUpdate = updateEmptyTransactionId(expense).get();
            expenseToUpdate.setTransactionId(expense.getTransactionId());
            return expenseRepository.save(expenseToUpdate);
        }

        Expense finalExpense = expense;
        List<Expense> byTransactionId = getByTransactionId(expense.getTransactionId());
        List<Expense> expenses1 = byTransactionId
                .stream()
                .filter(expense1 -> expense1.getTransactionValue().equals(finalExpense.getTransactionValue())).toList();
        List<Expense> duplicates = expenses1.stream()
                .filter(expense2 -> expense2.getTransactionTitle().equals(finalExpense.getTransactionTitle())).toList();


        LinkedList<Expense> linkedExpenses = new LinkedList<>(duplicates);
        if (linkedExpenses.size() > 0) {
            return removeDuplicates(linkedExpenses);
        }

        Expense expense1 = duplicates.stream().findAny().orElseGet(
                () -> expenseRepository.save(finalExpense));
        expenseRepository.save(expense1);
        categoryService.assignExpenseToDefaultCategory(expense1);
        banService.assingExpenseByBanName(expense1);
        return expense;
    }

    public Optional<Expense> updateEmptyTransactionId(Expense inputExpense) {
        List<Expense> toCheckOrUpdateList = getByTransactionId("toCheckOrUpdate");
        return toCheckOrUpdateList.stream()
                .filter(expense1 -> expense1.getTransactionData().equals(inputExpense.getTransactionData()))
                .filter(expense1 -> expense1.getTransactionValue().equals(inputExpense.getTransactionValue()))
                .findFirst();
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

    // TODO
    // to optimize
    public Map<String, BigDecimal> sumExpensesByCategoryInMonth(String yearMonth) {
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

    // TODO
    //  to optimize
    public Map<String, BigDecimal> sumExpensesByBanInMonth(String yearMonth) {
        Map<String, BigDecimal> banMap = new HashMap<>();
        banService.getAll().stream().forEach(bankAccountNumber -> {
            List<Expense> expenses = expensesByBanInMonth(bankAccountNumber, yearMonth);
            if (expenses.size() > 0) {
                BigDecimal sumValueOfExpenses = sumValueOfExpenses(expenses);
                banMap.put(bankAccountNumber.getName(), sumValueOfExpenses);
                System.out.println("categoryMap.size() = " + banMap.size());
            }

        });
        return banMap;
    }

    public List<Expense> expensesByCategoryInMonth(Category category, String yearMM) {
        return expensesInMonth(category.getExpenseList().stream(), yearMM);
    }

    public List<Expense> expensesByBanInMonth(BankAccountNumber ban, String yearMM) {
        return expensesInMonth(ban.getExpenses().stream(), yearMM);
    }

    public List<Expense> expensesByCategoryNameInMonth(String categoryName, String dateStr) {
        Category category = categoryService.getByName(categoryName);
        List<Expense> expenses = expensesByCategoryInMonth(category, dateStr);
        return expenses;
    }

    public Map<Category, List<Expense>> expensesInAllCategories(String dateStr) throws IOException {
        Map<Category, List<Expense>> categoryExpenseMap = new HashMap<>();
        categoryService.getAll().stream().forEach(category -> {
            List<Expense> expenses = expensesInMonth(category.getExpenseList().stream(), dateStr);
            categoryExpenseMap.put(category, expenses);
        });
        CsvHelper csvHelper = new CsvHelper();
        csvHelper.ExpenseAndCategoryToCSV(categoryExpenseMap);
        return categoryExpenseMap;
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
        if (data.contains(".")) {
            formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        }

        if (data.length() <= 6) {
            formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            data = data + "01";
        }
        LocalDate parse = LocalDate.parse(data, formatter);
        return parse;
    }

    private Expense removeDuplicates(LinkedList<Expense> expenseLinkedList) {
        Expense expense = expenseLinkedList.removeFirst();

        banService.releaseAssignedOneExpense(expense);
        categoryService.releaseOneExpenseFromCategory(expense);
        expenseRepository.deleteAll(expenseLinkedList);
        return expense;
    }


    public void removeAll() {
        getAllAssignedExpensesToBan().keySet().forEach(ban -> banService.releaseAssignedExpenses(ban.getName()));

        categoryService.getAll().stream().forEach(category -> categoryService.releaseAllExpensesFromCategory(category));
        expenseRepository.deleteAll(getAll());
    }


}
