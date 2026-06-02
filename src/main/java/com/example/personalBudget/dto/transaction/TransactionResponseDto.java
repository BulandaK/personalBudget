package com.example.personalBudget.dto.transaction;

import com.example.personalBudget.model.Category;
import com.example.personalBudget.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Detailed response information representing a registered transaction and its current category budget impacts")
public record TransactionResponseDto(

        @Schema(description = "The unique database identifier of the transaction", example = "45")
        Long id,

        @Schema(description = "The monetary value of the transaction", example = "250.50")
        BigDecimal amount,

        @Schema(description = "The type classification of the transaction", example = "EXPENSE")
        TransactionType type,

        @Schema(description = "The specific spending or income category", example = "FOOD")
        Category category,

        @Schema(description = "The description or note assigned to this entry", example = "Weekly grocery shopping at Lidl")
        String description,

        @Schema(description = "The registered date and time of the transaction", example = "2026-06-02T12:00:00")
        LocalDateTime date,

        @Schema(description = "The identifier of the associated account", example = "1")
        Long accountId,

        @Schema(description = "Flag indicating if the current month's budget limit for this category has been exceeded after inserting this transaction", example = "true")
        boolean budgetExceeded,

        @Schema(description = "Total accumulated spending amount in this specific category for the current month so far", example = "1450.75")
        BigDecimal spentInCategory
) {
}