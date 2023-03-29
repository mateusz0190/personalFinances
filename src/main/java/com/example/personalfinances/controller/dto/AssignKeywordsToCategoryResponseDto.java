package com.example.personalfinances.controller.dto;

import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Keyword;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AssignKeywordsToCategoryResponseDto {
    private List<String> usedKeyword;
    private List<String> correctlyAssignedKeyword;
    private String categoryName;
}
