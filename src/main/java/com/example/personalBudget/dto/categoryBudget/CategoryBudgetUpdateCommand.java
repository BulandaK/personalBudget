package com.example.personalBudget.dto.categoryBudget;

import com.example.personalBudget.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Data required to update an existing category spending limit")
public record CategoryBudgetUpdateCommand (

        @Schema(description = "ID of the account associated with the budget", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Account ID cannot be null")
        Long accountId,

        @Schema(description = "The targeted budget category to modify", example = "ENTERTAINMENT", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Category cannot be null")
        Category category,

        @Schema(description = "The new maximum spending threshold. Must be greater than zero.", example = "650.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Limit amount cannot be null")
        @Positive(message = "Limit amount must be greater than zero")
        BigDecimal limitAmount
){
}