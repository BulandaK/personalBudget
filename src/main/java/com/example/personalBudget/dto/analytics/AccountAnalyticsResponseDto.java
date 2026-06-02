package com.example.personalBudget.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Response data containing comprehensive financial analytics and summaries for a specific account")
public record AccountAnalyticsResponseDto(

        @Schema(description = "The analyzed account identifier", example = "1")
        Long accountId,

        @Schema(description = "The time range period for which the analytics were calculated")
        PeriodDto period,

        @Schema(description = "Total accumulated income within the requested period", example = "5200.00")
        BigDecimal totalIncome,

        @Schema(description = "Total accumulated expenses within the requested period", example = "1840.50")
        BigDecimal totalExpenses,

        @Schema(description = "Breakdown of total expenses grouped by each individual category")
        List<ExpenseByCategoryDto> expensesByCategory
) {
}