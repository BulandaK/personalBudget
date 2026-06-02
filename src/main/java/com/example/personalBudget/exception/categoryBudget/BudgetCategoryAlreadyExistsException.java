package com.example.personalBudget.exception.categoryBudget;

import com.example.personalBudget.exception.PersonalBudgetException;
import org.springframework.http.HttpStatus;

public class BudgetCategoryAlreadyExistsException extends PersonalBudgetException {
    public BudgetCategoryAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
