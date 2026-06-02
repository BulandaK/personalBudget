package com.example.personalBudget.exception.categoryBudget;

import com.example.personalBudget.exception.PersonalBudgetException;
import org.springframework.http.HttpStatus;

public class BudgetNotFoundException extends PersonalBudgetException {
    public BudgetNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
