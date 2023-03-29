package com.example.personalfinances.controller;

import com.example.personalfinances.controller.dto.GetMonthlyBilanceAllAccountsRequestDto;
import com.example.personalfinances.controller.dto.GetMonthlyBilanceAllAccountsResponseDto;
import com.example.personalfinances.helper.CsvHelper;
import com.example.personalfinances.model.Expense;
import com.example.personalfinances.service.CSVService;
import com.example.personalfinances.service.ExpenseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
        return ResponseEntity.ok(expenseService.getExpensesInMonth(date));
    }

    @GetMapping(value = "/monthly")
    public ResponseEntity<GetMonthlyBilanceAllAccountsResponseDto>
    getMonthlyBilanceAllAccounts(@RequestBody GetMonthlyBilanceAllAccountsRequestDto requestDto) {
        String date = requestDto.getYear() + requestDto.getMonthName();


        return null;
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> removeAll() {
        expenseService.removeAll();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
