package com.example.personalfinances.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;


@Builder
@Data
public class GetMonthlyBilanceAllAccountsResponseDto {
    private Map<String, BigDecimal> monthlyCategoryBilance;
    private  Map<String, BigDecimal> monthlyBanBilance;

}
