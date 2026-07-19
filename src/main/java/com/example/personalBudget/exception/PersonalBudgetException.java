package com.example.personalBudget.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class PersonalBudgetException extends RuntimeException {
    private final HttpStatus httpStatus;
    public PersonalBudgetException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
