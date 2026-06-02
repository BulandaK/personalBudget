package com.example.personalBudget.controller;

import com.example.personalBudget.dto.account.AccountResponseDto;
import com.example.personalBudget.dto.account.CreateAccountCommand;
import com.example.personalBudget.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Endpoints for managing personal accounts and their balances")
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    @Operation(
            summary = "Get all accounts",
            description = "Retrieves a full list of all registered personal accounts with their current balances."
    )
    @ApiResponse(responseCode = "200", description = "Account list successfully retrieved")
    public List<AccountResponseDto> getAll() {
        return accountService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new account",
            description = "Creates a new personal account with an initial name and balance."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or validation failed", content = @Content)
    })
    public AccountResponseDto create(
            @Valid @RequestBody CreateAccountCommand command
    ) {
        return accountService.create(command);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get account details",
            description = "Retrieves detailed information about a specific account by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found and details returned"),
            @ApiResponse(responseCode = "404", description = "Account with the given ID does not exist", content = @Content)
    })
    public AccountResponseDto getAccount(
            @PathVariable @Parameter(description = "Unique identifier of the account", example = "1") Long id
    ) {
        return accountService.getAccount(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete an account",
            description = "Deletes an account from the system. Deletion is only permitted if the account has no associated transactions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Account with the given ID does not exist", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cannot delete account because it contains related transactions", content = @Content)
    })
    public void deleteAccount(
            @PathVariable @Parameter(description = "Unique identifier of the account to delete", example = "1") Long id
    ) {
        accountService.delete(id);
    }
}
