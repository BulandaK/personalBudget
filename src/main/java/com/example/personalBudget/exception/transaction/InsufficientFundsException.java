package com.example.personalBudget.exception.transaction;

import com.example.personalBudget.exception.PersonalBudgetException;
import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends PersonalBudgetException {
    public InsufficientFundsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
