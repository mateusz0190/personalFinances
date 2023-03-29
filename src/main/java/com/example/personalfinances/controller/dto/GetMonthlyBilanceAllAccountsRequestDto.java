package com.example.personalfinances.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class GetMonthlyBilanceAllAccountsRequestDto {
    private String monthName;
    private String year;

}
