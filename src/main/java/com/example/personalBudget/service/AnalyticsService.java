package com.example.personalBudget.service;

import com.example.personalBudget.dto.analytics.*;
import com.example.personalBudget.model.Category;
import com.example.personalBudget.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final TransactionRepository transactionRepository;

    public AccountAnalyticsResponseDto getAccountAnalytics(Long accountId, LocalDateTime fromDate, LocalDateTime toDate) {

        BigDecimal income = transactionRepository.sumIncome(accountId, fromDate, toDate);
        BigDecimal expenses = transactionRepository.sumExpenses(accountId, fromDate, toDate);

        if (income == null) income = BigDecimal.ZERO;
        if (expenses == null) expenses = BigDecimal.ZERO;

        BigDecimal finalExpenses = expenses;
        List<ExpenseByCategoryDto> byCategory =
                transactionRepository.sumExpensesByCategory(accountId, fromDate, toDate)
                        .stream()
                        .map(p -> {
                            BigDecimal amount = p.getAmount() == null ? BigDecimal.ZERO : p.getAmount();

                            BigDecimal percentage = finalExpenses.compareTo(BigDecimal.ZERO) == 0
                                    ? BigDecimal.ZERO
                                    : amount.abs()
                                    .divide(finalExpenses.abs(), 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100));

                            return new ExpenseByCategoryDto(
                                    p.getCategory(),
                                    amount,
                                    percentage
                            );
                        })
                        .toList();

        return new AccountAnalyticsResponseDto(
                accountId,
                new PeriodDto(fromDate, toDate),
                income,
                expenses,
                byCategory
        );
    }
}