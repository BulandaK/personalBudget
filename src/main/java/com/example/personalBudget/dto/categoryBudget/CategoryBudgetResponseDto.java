package com.example.personalBudget.dto.categoryBudget;

import com.example.personalBudget.model.Category;

import java.math.BigDecimal;

public record CategoryBudgetResponseDto(
        Long id,
        Long accountId,
        Category category,
        BigDecimal limitAmount
) {
}
