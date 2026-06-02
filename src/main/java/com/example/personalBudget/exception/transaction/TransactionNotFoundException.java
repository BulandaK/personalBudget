package com.example.personalBudget.exception.transaction;

import com.example.personalBudget.exception.PersonalBudgetException;
import org.springframework.http.HttpStatus;

public class TransactionNotFoundException extends PersonalBudgetException {
    public TransactionNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
