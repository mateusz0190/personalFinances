package com.example.personalfinances.controller.dto;

import com.example.personalfinances.model.Category;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Data
public class GetMonthlyBilanceAllAccountsResponseDto {
    private Map<Category, BigDecimal> bilanceMap;
}
