package com.example.personalBudget.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateAccountCommand(
        @Schema(description = "The unique name of the account",example = "my account", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String name
) {
}
