package com.example.personalfinances.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class AssignKeywordsToCategoryRequestDto {
    private String categoryName;
    private List<String> keywordNames;
}
