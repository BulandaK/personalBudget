package com.example.personalBudget.controller;

import com.example.personalBudget.dto.categoryBudget.CategoryBudgetResponseDto;
import com.example.personalBudget.dto.categoryBudget.CategoryBudgetUpdateCommand;
import com.example.personalBudget.dto.categoryBudget.CreateCategoryBudgetCreateCommand;
import com.example.personalBudget.service.CategoryBudgetService;
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
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Tag(name = "Category Budgets", description = "Endpoints for managing monthly spending limits for specific account categories")
public class CategoryBudgetController {
    private final CategoryBudgetService categoryBudgetService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(
            summary = "Set a new category budget limit",
            description = "Defines a fresh spending limit for a specific category on a selected account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Budget limit successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters or validation failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "The specified account does not exist", content = @Content)
    })
    public CategoryBudgetResponseDto setCategoryLimit(@RequestBody @Valid CreateCategoryBudgetCreateCommand command) {
        return categoryBudgetService.setLimit(command);
    }

    @GetMapping("/account/{accountId}")
    @Operation(
            summary = "Get all budgets for an account",
            description = "Retrieves a list of all defined category limits associated with a specific account ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of budgets successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Account with the given ID does not exist", content = @Content)
    })
    public List<CategoryBudgetResponseDto> getBudgets(
            @PathVariable @Parameter(description = "ID of the account to fetch budgets for", example = "1") Long accountId
    ) {
        return categoryBudgetService.getByAccount(accountId);
    }

    @PutMapping
    @Operation(
            summary = "Update an existing category budget limit",
            description = "Modifies the limit amount for an already existing account budget category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget limit successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or negative limit amount", content = @Content),
            @ApiResponse(responseCode = "404", description = "Budget for the specified account and category combination was not found", content = @Content)
    })
    public CategoryBudgetResponseDto updateLimit(@RequestBody @Valid CategoryBudgetUpdateCommand command) {
       return categoryBudgetService.updateLimit(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a budget limit",
            description = "Removes a specific category budget limit from the system completely using its unique database ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Budget limit successfully removed"),
            @ApiResponse(responseCode = "404", description = "Budget limit with the given ID does not exist", content = @Content)
    })
    public void deleteLimit(
            @PathVariable @Parameter(description = "Unique identifier of the budget limit configuration", example = "1") Long id
    ) {
        categoryBudgetService.delete(id);
    }

}
