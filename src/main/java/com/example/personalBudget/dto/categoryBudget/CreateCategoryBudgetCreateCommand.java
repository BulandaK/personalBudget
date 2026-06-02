package com.example.personalBudget.dto.categoryBudget;

import com.example.personalBudget.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Data required to set a new spending limit configuration for a category")
public record CreateCategoryBudgetCreateCommand(

        @Schema(description = "ID of the account to assign the budget to", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Account ID cannot be null")
        Long accountId,

        @Schema(description = "The target category for the limit", example = "FOOD", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Category cannot be null")
        Category category,

        @Schema(description = "Maximum allowed spending amount for this category. Must be greater than zero.", example = "1200.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Limit amount cannot be null")
        @Positive(message = "Limit amount must be greater than zero")
        BigDecimal limitAmount
) {
}