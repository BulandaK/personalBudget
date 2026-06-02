package com.example.personalBudget.exception.account;

import com.example.personalBudget.exception.PersonalBudgetException;
import org.springframework.http.HttpStatus;

public class AccountAlreadyExistsException extends PersonalBudgetException {
    public AccountAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
