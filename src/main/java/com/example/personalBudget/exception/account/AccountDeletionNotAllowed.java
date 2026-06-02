package com.example.personalBudget.exception.account;

import com.example.personalBudget.exception.PersonalBudgetException;
import org.springframework.http.HttpStatus;

public class AccountDeletionNotAllowed extends PersonalBudgetException {
    public AccountDeletionNotAllowed(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
