package com.example.personalBudget.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Response data containing details of a personal account and its current balance")
public record AccountResponseDto(

        @Schema(description = "The unique database identifier of the account", example = "1")
        Long id,

        @Schema(description = "The name given to the account", example = "Main Checking Account")
        String name,

        @Schema(description = "The current monetary balance available in the account", example = "3500.00")
        BigDecimal balance
) {
}