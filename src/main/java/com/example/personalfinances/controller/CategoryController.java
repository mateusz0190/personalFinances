package com.example.personalfinances.controller;

import com.example.personalfinances.controller.dto.AssignKeywordsToCategoryRequestDto;
import com.example.personalfinances.controller.dto.AssignKeywordsToCategoryResponseDto;
import com.example.personalfinances.controller.dto.GetMonthlyBilanceAllAccountsRequestDto;
import com.example.personalfinances.controller.dto.GetMonthlyBilanceAllAccountsResponseDto;
import com.example.personalfinances.helper.CsvHelper;
import com.example.personalfinances.model.Category;
import com.example.personalfinances.service.CSVService;
import com.example.personalfinances.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


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


    @PutMapping
    public ResponseEntity<AssignKeywordsToCategoryResponseDto>
    assignKeywordToCategory(@RequestBody AssignKeywordsToCategoryRequestDto requestDto) {
        AssignKeywordsToCategoryResponseDto responseDto = categoryService.AssignKeywordToCategory(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/assign_expenses")
    public ResponseEntity<HttpStatus> assignExpensesToDefaultCategory() {
        categoryService.assignUncategorizedExpensesToDefaultCategory();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping(value = "/release/{categoryName}/{kName}")
    public ResponseEntity<Category> releaseKeyword(
            @PathVariable("categoryName") String categoryName, @PathVariable("kName") String kName) {
        return ResponseEntity.ok(categoryService.releaseKeywordFromCategory(categoryName, kName));
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping(value = "/{kName}")
    public ResponseEntity<Category> getCategoryByKeyword(@PathVariable("kName") String kName) {
        Optional<Category> byKeyword = categoryService.getByKeyword(kName);
        if (byKeyword.isPresent()) {
            return ResponseEntity.ok(byKeyword.get());
        } else
            return ResponseEntity.ok(byKeyword.orElse(new Category()));
    }



    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HttpStatus> removeCategoryById(@PathVariable("id") long id) {
        if (categoryService.CategoryisPresentByID(id)) {
            categoryService.removeById(id);
            return ResponseEntity.ok(HttpStatus.OK);
        }
        return ResponseEntity.ok(HttpStatus.NOT_FOUND);

    }
}
