package com.example.personalBudget.dto.analytics;

import com.example.personalBudget.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Aggregated details of expenses for a specific category, including its percentage share of total spending")
public record ExpenseByCategoryDto(

        @Schema(description = "The financial category of the expense", example = "FOOD")
        Category category,

        @Schema(description = "The total monetary amount spent in this category within the period", example = "450.75")
        BigDecimal amount,

        @Schema(description = "The percentage share of this category relative to the total expenses (0.00 - 100.00)", example = "24.50")
        BigDecimal percentage
) {
}