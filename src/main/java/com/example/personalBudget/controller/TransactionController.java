package com.example.personalBudget.controller;

import com.example.personalBudget.dto.transaction.CreateTransactionCommand;
import com.example.personalBudget.dto.transaction.TransactionFilterCriteria;
import com.example.personalBudget.dto.transaction.TransactionResponseDto;
import com.example.personalBudget.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints for creating, deleting, and retrieving financial transactions with pagination")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    @Operation(
            summary = "Get paginated transactions",
            description = "Retrieves a paginated list of transactions, optionally filtered by date range and category."
    )
    @ApiResponse(responseCode = "200", description = "Paginated list of transactions successfully retrieved")
    public Page<TransactionResponseDto> getTransactions(
            @Valid TransactionFilterCriteria criteria,
            @Parameter(description = "Pagination and sorting parameters (e.g., page=0, size=10, sort=date,desc)")
            Pageable pageable
    ) {
        return transactionService.getTransactions(criteria,pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new transaction",
            description = "Registers a new financial transaction (Income or Expense). If it is an Expense, it also evaluates the category budget status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or budget/account processing failure", content = @Content),
            @ApiResponse(responseCode = "404", description = "The specified account ID does not exist", content = @Content)
    })
    public TransactionResponseDto create(@RequestBody @Valid CreateTransactionCommand command) {
        return transactionService.create(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a transaction",
            description = "Removes a specific transaction by its unique database ID and automatically reverts its impact on the account balance."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Transaction with the given ID does not exist", content = @Content)
    })
    public void delete(
            @PathVariable @NotNull @Parameter(description = "Unique identifier of the transaction", example = "45") Long id
    ) {
        transactionService.delete(id);
    }
}
