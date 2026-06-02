package com.example.personalBudget.controller;

import com.example.personalBudget.dto.analytics.AccountAnalyticsResponseDto;
import com.example.personalBudget.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@Tag(name = "Analytics", description = "Endpoints for financial data aggregation, summaries, and spending analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/{accountId}/analytics")
    @Operation(
            summary = "Get financial analytics for an account",
            description = "Calculates total income, total expenses, and groups expenses by category within a specified time range for a given account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Financial analytics successfully calculated and retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid date-time format or missing request parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account with the specified ID was not found", content = @Content)
    })
    public AccountAnalyticsResponseDto getAccountAnalytics(
            @PathVariable
            @Parameter(description = "ID of the account to analyze", example = "1")
            Long accountId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Start date-time of the analysis period (ISO format)", example = "2026-06-01T00:00:00", required = true)
            LocalDateTime fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "End date-time of the analysis period (ISO format)", example = "2026-06-30T23:59:59", required = true)
            LocalDateTime toDate
    ) {
        return analyticsService.getAccountAnalytics(accountId, fromDate, toDate);
    }
}
