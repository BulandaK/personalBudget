package com.example.personalBudget.dto.transaction;

import com.example.personalBudget.model.Category;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "Criteria parameters used for filtering the paginated transaction list")
public record TransactionFilterCriteria(

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @Parameter(description = "Filter start date-time (ISO format)", example = "2026-06-01T00:00:00")
        LocalDateTime from,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @Parameter(description = "Filter end date-time (ISO format)", example = "2026-06-30T23:59:59")
        LocalDateTime to,

        @Parameter(description = "Filter by specific transaction category", example = "FOOD")
        Category category
) {
}