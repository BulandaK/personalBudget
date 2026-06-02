package com.example.personalBudget.service;

import com.example.personalBudget.dto.transaction.CreateTransactionCommand;
import com.example.personalBudget.dto.transaction.TransactionFilterCriteria;
import com.example.personalBudget.dto.transaction.TransactionResponseDto;
import com.example.personalBudget.exception.transaction.TransactionNotFoundException;
import com.example.personalBudget.mapper.TransactionMapper;
import com.example.personalBudget.model.Account;
import com.example.personalBudget.model.Category;
import com.example.personalBudget.model.Transaction;
import com.example.personalBudget.model.TransactionType;
import com.example.personalBudget.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    AccountService accountService;
    CategoryBudgetService categoryBudgetService;
    TransactionRepository transactionRepository;
    TransactionMapper transactionMapper;
    TransactionService transactionService;

    @BeforeEach
    void setup() {
        this.accountService = Mockito.mock(AccountService.class);
        this.categoryBudgetService = Mockito.mock(CategoryBudgetService.class);
        this.transactionRepository = Mockito.mock(TransactionRepository.class);
        this.transactionMapper = Mappers.getMapper(TransactionMapper.class);
        this.transactionService = new TransactionService(accountService, categoryBudgetService, transactionRepository, transactionMapper);
    }

    @Test
    void create_TransactionIsIncome_ReturnsDtoWithoutCheckingBudget() {
        LocalDateTime now = LocalDateTime.now();
        CreateTransactionCommand command = new CreateTransactionCommand(
                new BigDecimal("1000.00"), TransactionType.INCOME, Category.SALARY, "Monthly salary", now, 1L
        );
        Account mockAccount = new Account(1L, "Main Account", new BigDecimal("2500.00"), null);
        Transaction savedTransaction = new Transaction(
                55L, command.amount(), command.transactionType(), command.category(), command.description(), command.date(), mockAccount
        );
        when(accountService.updateBalance(1L, command.amount(), TransactionType.INCOME)).thenReturn(mockAccount);
        when(transactionRepository.save(any())).thenReturn(savedTransaction);

        TransactionResponseDto result = transactionService.create(command);

        Assertions.assertAll(
                () -> assertEquals(55L, result.id()),
                () -> assertEquals(new BigDecimal("1000.00"), result.amount()),
                () -> assertEquals(TransactionType.INCOME, result.type()),
                () -> assertFalse(result.budgetExceeded()),
                () -> assertEquals(BigDecimal.ZERO, result.spentInCategory())
        );

        verify(transactionRepository, never()).sumExpensesForSpecificCategory(any(), any(), any(), any());
        verify(categoryBudgetService, never()).isBudgetExceeded(any(), any(), any(), any());
    }

    @Test
    void create_TransactionIsExpense_ChecksBudgetAndReturnsDtoWithBudgetData() {
        LocalDateTime now = LocalDateTime.now();
        CreateTransactionCommand command = new CreateTransactionCommand(
                new BigDecimal("150.00"), TransactionType.EXPENSE, Category.FOOD, "Grocery shopping", now, 1L
        );
        Account mockAccount = new Account(1L, "Main Account", new BigDecimal("2350.00"), null);
        Transaction savedTransaction = new Transaction(
                56L, command.amount(), command.transactionType(), command.category(), command.description(), command.date(), mockAccount
        );
        BigDecimal currentSpent = new BigDecimal("450.00");

        when(accountService.updateBalance(1L, command.amount(), TransactionType.EXPENSE)).thenReturn(mockAccount);
        when(transactionRepository.save(any())).thenReturn(savedTransaction);

        when(transactionRepository.sumExpensesForSpecificCategory(
                eq(1L), eq(Category.FOOD), any(), any()
        )).thenReturn(currentSpent);

        when(categoryBudgetService.isBudgetExceeded(1L, Category.FOOD, TransactionType.EXPENSE, currentSpent))
                .thenReturn(true);

        TransactionResponseDto result = transactionService.create(command);

        Assertions.assertAll(
                () -> assertEquals(56L, result.id()),
                () -> assertEquals(TransactionType.EXPENSE, result.type()),
                () -> assertTrue(result.budgetExceeded()),
                () -> assertEquals(new BigDecimal("450.00"), result.spentInCategory())
        );

        verify(transactionRepository, times(1)).sumExpensesForSpecificCategory(eq(1L), eq(Category.FOOD), any(), any());
        verify(categoryBudgetService, times(1)).isBudgetExceeded(1L, Category.FOOD, TransactionType.EXPENSE, currentSpent);
    }

    @Test
    void delete_TransactionIsIncome_ReversesBalanceWithExpenseAndDeletes() {
        Long transactionId = 10L;
        Long accountId = 1L;
        Account mockAccount = new Account(accountId, "Main Account", new BigDecimal("1000.00"), null);

        Transaction incomeTransaction = new Transaction(
                transactionId, new BigDecimal("200.00"), TransactionType.INCOME,
                Category.SALARY, "Bonus", LocalDateTime.now(), mockAccount
        );

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(incomeTransaction));
        when(accountService.updateBalance(eq(accountId), eq(new BigDecimal("200.00")), eq(TransactionType.EXPENSE)))
                .thenReturn(mockAccount);

        transactionService.delete(transactionId);

        verify(accountService, times(1)).updateBalance(accountId, new BigDecimal("200.00"), TransactionType.EXPENSE);
        verify(transactionRepository, times(1)).delete(incomeTransaction);
    }

    @Test
    void delete_TransactionIsExpense_ReversesBalanceWithIncomeAndDeletes() {
        Long transactionId = 11L;
        Long accountId = 1L;
        Account mockAccount = new Account(accountId, "Main Account", new BigDecimal("800.00"), null);

        Transaction expenseTransaction = new Transaction(
                transactionId, new BigDecimal("50.00"), TransactionType.EXPENSE,
                Category.FOOD, "Dinner", LocalDateTime.now(), mockAccount
        );

        when(transactionRepository.findById(any())).thenReturn(Optional.of(expenseTransaction));
        when(accountService.updateBalance(eq(accountId), eq(new BigDecimal("50.00")), eq(TransactionType.INCOME)))
                .thenReturn(mockAccount);

        transactionService.delete(transactionId);

        verify(accountService, times(1)).updateBalance(accountId, new BigDecimal("50.00"), TransactionType.INCOME);
        verify(transactionRepository, times(1)).delete(expenseTransaction);
    }

    @Test
    void delete_TransactionDoesNotExist_ThrowsTransactionNotFoundException() {
        Long nonExistingTransactionId = 999L;
        when(transactionRepository.findById(any())).thenReturn(Optional.empty());

        TransactionNotFoundException exception = assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.delete(nonExistingTransactionId)
        );

        assertEquals("Transaction not found", exception.getMessage());
        verify(accountService, never()).updateBalance(any(), any(), any());
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void getTransactions_CriteriaMatchData_ReturnsPageOfTransactionResponseDtos() {
        TransactionFilterCriteria criteria = new TransactionFilterCriteria(LocalDateTime.now().minusDays(5), LocalDateTime.now(), Category.FOOD);
        Pageable pageable = PageRequest.of(0, 10);

        Account mockAccount = new Account(1L, "Main Account", new BigDecimal("1000.00"), null);
        Transaction transaction1 = new Transaction(101L, new BigDecimal("50.00"), TransactionType.EXPENSE, Category.FOOD, "Lidl", LocalDateTime.now(), mockAccount);

        List<Transaction> transactionsList = List.of(transaction1);
        Page<Transaction> mockPage = new PageImpl<>(transactionsList, pageable, transactionsList.size());
        when(transactionRepository.findWithFilters(eq(criteria.from()), eq(criteria.to()), eq(criteria.category()), eq(pageable))).thenReturn(mockPage);

        Page<TransactionResponseDto> result = transactionService.getTransactions(criteria, pageable);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals(101L, result.getContent().getFirst().id()),
                () -> assertEquals(new BigDecimal("50.00"), result.getContent().getFirst().amount()),
                () -> assertEquals("Lidl", result.getContent().getFirst().description())
        );
    }

    @Test
    void getTransactions_NoTransactionsFound_ReturnsEmptyPage() {
        TransactionFilterCriteria criteria = new TransactionFilterCriteria(null, null, Category.ENTERTAINMENT);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> emptyMockPage = new PageImpl<>(List.of(), pageable, 0);
        when(transactionRepository.findWithFilters(any(), any(), any(), eq(pageable))).thenReturn(emptyMockPage);

        Page<TransactionResponseDto> result = transactionService.getTransactions(criteria, pageable);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(0, result.getTotalElements()),
                () -> assertTrue(result.getContent().isEmpty())
        );
    }
}
