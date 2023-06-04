package com.example.personalfinances.controller.dto;

import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Expense;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class GetCategoryAndExpenseListResponseDto {
    private Map<Category, List<Expense>> categoryListMap;
}
