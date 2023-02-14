package com.example.personalfinances.service;

import com.example.personalfinances.helper.CsvHelper;
import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Expense;
import com.example.personalfinances.repository.CategoryRepository;
import com.example.personalfinances.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class CSVService {
    private ExpenseRepository expenseRepository;
    private CategoryRepository categoryRepository;


    public void saveExpenses(MultipartFile multipartFile) throws IOException {
        try {
            List<Expense> expenseEntities = CsvHelper.CSVtoExpense(multipartFile.getInputStream());
            expenseRepository.saveAll(expenseEntities);

        } catch (IOException e) {
            throw new RuntimeException("problemm" + e.getMessage());
        }
    }

    public void saveCategories(MultipartFile multipartFile) {
        try {
            Set<Category> categories = CsvHelper.CSVtoCategory(multipartFile.getInputStream());
            categoryRepository.saveAll(categories);

        } catch (IOException e) {
            throw new RuntimeException("problemm" + e.getMessage());
        }
    }
}

