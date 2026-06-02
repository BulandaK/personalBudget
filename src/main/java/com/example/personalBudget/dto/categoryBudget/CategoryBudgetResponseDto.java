package com.example.personalBudget.dto.categoryBudget;

import com.example.personalBudget.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record CategoryBudgetResponseDto(
        @Schema(description = "Unique identifier of the category budget record", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(description = "ID of the account associated with this budget", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long accountId,

        @Schema(description = "The financial category under budget control", example = "ENTERTAINMENT", requiredMode = Schema.RequiredMode.REQUIRED)
        Category category,

        @Schema(description = "The maximum allowed spending limit for this category", example = "650.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal limitAmount
) {
}
