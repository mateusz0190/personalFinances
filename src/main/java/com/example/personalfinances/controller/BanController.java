package com.example.personalfinances.controller;

import com.example.personalfinances.model.BankAccountNumber;
import com.example.personalfinances.service.BanService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/bankAccount")
public class BanController {
    private BanService banService;

    @PostMapping
    public ResponseEntity<BankAccountNumber> createAccount(@RequestBody BankAccountNumber ban) {
        banService.createAccount(ban);
        return ResponseEntity.ok(banService.createAccount(ban));
    }

    @GetMapping
    public ResponseEntity<List<BankAccountNumber>> getAll() {
        return ResponseEntity.ok(banService.getAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity<BankAccountNumber> getByName(@PathVariable("name") String name) {
        return ResponseEntity.ok(banService.getByAccountName(name));
    }

    @PutMapping("/{alias}")
    public ResponseEntity<BankAccountNumber>
    assignExpenses(@PathVariable("alias") String id) {
        return ResponseEntity.ok(banService.assingExpense(id));
    }
}
