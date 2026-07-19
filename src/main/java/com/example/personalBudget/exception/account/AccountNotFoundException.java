package com.example.personalBudget.exception.account;

import com.example.personalBudget.exception.PersonalBudgetException;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends PersonalBudgetException {
    public AccountNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
