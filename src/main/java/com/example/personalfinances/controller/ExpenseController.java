package com.example.personalfinances.controller;

import com.example.personalfinances.controller.dto.GetCategoryAndExpenseListResponseDto;
import com.example.personalfinances.controller.dto.GetMonthlyBilanceAllAccountsRequestDto;
import com.example.personalfinances.controller.dto.GetMonthlyBilanceAllAccountsResponseDto;
import com.example.personalfinances.helper.CsvHelper;
import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Expense;
import com.example.personalfinances.service.CSVService;
import com.example.personalfinances.service.ExpenseService;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/expense")
public class ExpenseController {
    private ExpenseService expenseService;
    private CSVService csvService;

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.createExpense(expense));
    }

    @PostMapping(value = "/csv")
    public ResponseEntity<HttpStatus> importExpense(@RequestParam("file") MultipartFile file) throws IOException {
        if (CsvHelper.hasCSVFormat(file)) {
            try {
                csvService.saveExpenses(file);
                return ResponseEntity.ok(HttpStatus.CREATED);

            } catch (IOException e) {
                return (ResponseEntity<HttpStatus>) ResponseEntity.status(HttpStatus.CONFLICT);
            }
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAll() {
        return ResponseEntity.ok(expenseService.getAll());
    }

    @GetMapping(value = "/{tId}")
    public ResponseEntity<Expense> getByTransactionId(@PathVariable("tId") String transactionId) {
        return ResponseEntity.ok(expenseService.getByTransactionId(transactionId).get(0));
    }

    @GetMapping(value = "/monthly/{yearMonth}")
    public ResponseEntity<List<Expense>> getExpensesInMonthInYear(@PathVariable("yearMonth") String date) {
        return ResponseEntity.ok(expenseService.getAllExpensesInMonth(date));
    }

    @GetMapping(value = "/monthly")
    public ResponseEntity<GetMonthlyBilanceAllAccountsResponseDto>
    getMonthlyBilanceAllAccounts(@RequestBody GetMonthlyBilanceAllAccountsRequestDto requestDto) {
        String date = requestDto.getYear() + requestDto.getMonthName();
        Map<String, BigDecimal> categoryBigDecimalMap = expenseService.sumExpensesByCategoryInMonth(date);
        Map<String, BigDecimal> banBigDecimalMap = expenseService.sumExpensesByBanInMonth(date);
        BigDecimal bigDecimal = expenseService.sumValueOfExpenses(expenseService.getAllExpensesInMonth(date));
        return ResponseEntity.ok(GetMonthlyBilanceAllAccountsResponseDto.builder()
                .monthlyCategoryBilance(categoryBigDecimalMap)
                .monthlyBanBilance(banBigDecimalMap)
                .overall(bigDecimal)
                .build());
    }

    @GetMapping(value = "/monthly/{date1}/{categoryName}")
    public ResponseEntity<List<Expense>> getExpensesByCategoryInMonth(
            @PathVariable("categoryName") String categoryName, @PathVariable("date1") String date1) {
        List<Expense> expenses = expenseService.expensesByCategoryNameInMonth(categoryName, date1);

        return ResponseEntity.ok(expenses);
    }

    // TODO
    @GetMapping(value = "/expense/report/{date}")
    public ResponseEntity<HttpStatus> getMonthlyCategoryAndExpenseListReportToCSV(
            @PathVariable("date") String date) throws IOException {
        Map<Category, List<Expense>> stringListMap = expenseService.expensesInAllCategories(date);
        return ResponseEntity.ok(HttpStatus.OK);
    }
//@PutMapping
//public ResponseEntity<List<Expense>> mergeTransactionId(){
//        return ResponseEntity.ok(expenseService.updateEmptyTransactionId());
//}
    @DeleteMapping
    public ResponseEntity<HttpStatus> removeAll() {
        expenseService.removeAll();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
