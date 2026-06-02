package com.example.personalBudget.dto.transaction;

import com.example.personalBudget.model.Category;
import com.example.personalBudget.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Data required to create a new income or expense transaction")
public record CreateTransactionCommand(

        @Schema(description = "The monetary value of the transaction. Must be positive.", example = "250.50", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @Schema(description = "The type of the transaction", example = "EXPENSE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Transaction type cannot be null")
        TransactionType transactionType,

        @Schema(description = "The financial category associated with this transaction", example = "FOOD", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Category cannot be null")
        Category category,

        @Schema(description = "Optional custom description or notes regarding the transaction", example = "Weekly grocery shopping at Lidl", maxLength = 255)
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,

        @Schema(description = "The date and time when the transaction occurred (ISO format)", example = "2026-06-02T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Date cannot be null")
        LocalDateTime date,

        @Schema(description = "The database identifier of the account this transaction belongs to", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Account ID cannot be null")
        Long accountId
) {
}