package com.example.personalBudget.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "The precise date and time boundaries used to filter and aggregate the financial data")
public record PeriodDto(

        @Schema(description = "The starting date-time boundary of the analysis period (inclusive)", example = "2026-06-01T00:00:00")
        LocalDateTime fromDate,

        @Schema(description = "The ending date-time boundary of the analysis period (inclusive)", example = "2026-06-30T23:59:59")
        LocalDateTime toDate
) {
}