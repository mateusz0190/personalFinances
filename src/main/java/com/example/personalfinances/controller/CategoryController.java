package com.example.personalfinances.controller;

import com.example.personalfinances.helper.CsvHelper;
import com.example.personalfinances.service.CSVService;
import com.example.personalfinances.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController {
    private CategoryService categoryService;
    private CSVService csvService;

    @PostMapping(value = "/csv")
    public ResponseEntity<HttpStatus> importCategories(@RequestParam("file") MultipartFile file) {
        if (CsvHelper.hasCSVFormat(file)) {
            csvService.saveCategories(file);
            return ResponseEntity.ok(HttpStatus.CREATED);

        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
