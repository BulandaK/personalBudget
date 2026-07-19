package com.example.personalBudget.service;

import com.example.personalBudget.dto.transaction.CreateTransactionCommand;
import com.example.personalBudget.dto.transaction.TransactionFilterCriteria;
import com.example.personalBudget.dto.transaction.TransactionResponseDto;
import com.example.personalBudget.exception.transaction.TransactionNotFoundException;
import com.example.personalBudget.mapper.TransactionMapper;
import com.example.personalBudget.model.Account;
import com.example.personalBudget.model.Transaction;
import com.example.personalBudget.model.TransactionType;
import com.example.personalBudget.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountService accountService;
    private final CategoryBudgetService categoryBudgetService;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponseDto create(@Valid CreateTransactionCommand command) {
        Account account = accountService.updateBalance(command.accountId(), command.amount(),command.transactionType());
        Transaction transaction = new Transaction(null,command.amount(), command.transactionType(),command.category(), command.description(), command.date(),account);
        Transaction saved = transactionRepository.save(transaction);
        BudgetInfo budgetInfo = getBudgetInfo(command);
        return transactionMapper.toDtoWithBudget(saved,budgetInfo.budgetExceeded,budgetInfo.spentInCategory);
    }

    @Transactional
    public void delete(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        TransactionType reverseType =
                transaction.getType() == TransactionType.INCOME
                        ? TransactionType.EXPENSE
                        : TransactionType.INCOME;

        accountService.updateBalance(transaction.getAccount().getId(),transaction.getAmount(),reverseType);
        transactionRepository.delete(transaction);
    }

    public Page<TransactionResponseDto> getTransactions(TransactionFilterCriteria criteria, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findWithFilters(criteria.from(),criteria.to(),criteria.category(),pageable);
        return transactions.map(transactionMapper::toDto);
    }

    private BudgetInfo getBudgetInfo(CreateTransactionCommand command) {
        if (command.transactionType() != TransactionType.EXPENSE) {
            return new BudgetInfo(BigDecimal.ZERO, false);
        }

        LocalDateTime firstDayOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastDayOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);

        BigDecimal spentInCategory = transactionRepository.sumExpensesForSpecificCategory(
                command.accountId(),
                command.category(),
                firstDayOfMonth,
                lastDayOfMonth
        );

        boolean budgetExceeded = categoryBudgetService.isBudgetExceeded(
                command.accountId(),
                command.category(),
                command.transactionType(),
                spentInCategory
        );

        return new BudgetInfo(spentInCategory, budgetExceeded);
    }
    private record BudgetInfo(BigDecimal spentInCategory, boolean budgetExceeded) {}

}
