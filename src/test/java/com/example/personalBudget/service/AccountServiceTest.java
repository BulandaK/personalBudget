package com.example.personalBudget.service;

import com.example.personalBudget.dto.account.AccountResponseDto;
import com.example.personalBudget.dto.account.CreateAccountCommand;
import com.example.personalBudget.exception.account.AccountAlreadyExistsException;
import com.example.personalBudget.exception.account.AccountDeletionNotAllowed;
import com.example.personalBudget.exception.account.AccountNotFoundException;
import com.example.personalBudget.mapper.AccountMapper;
import com.example.personalBudget.model.Account;
import com.example.personalBudget.model.Transaction;
import com.example.personalBudget.model.TransactionType;
import com.example.personalBudget.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    AccountRepository accountRepository;
    AccountMapper accountMapper;
    AccountService accountService;

    @BeforeEach
    void setup() {
        this.accountMapper = Mappers.getMapper(AccountMapper.class);
        this.accountRepository = Mockito.mock(AccountRepository.class);
        this.accountService = new AccountService(accountRepository, accountMapper);
    }
    @Test
    void getAll_DataCorrect_ReturnsListAccountResponseDto() {
        Account account = new Account(1L, "my account", new BigDecimal("50.00"), null);
        List<Account> accountList = List.of(account);
        when(accountRepository.findAll()).thenReturn(accountList);

        List<AccountResponseDto> result = accountService.getAll();

        Assertions.assertAll(
                () -> assertEquals(1L, result.getFirst().id()),
                () -> assertEquals("my account", result.getFirst().name()),
                () -> assertEquals(new BigDecimal("50.00"), result.getFirst().balance())
        );
    }
    @Test
    void create_DataCorrect_ReturnsAccountResponseDto() {
        CreateAccountCommand command = new CreateAccountCommand("my new account");
        Account saved = new Account(1L, "my new account", new BigDecimal(0), null);
        when(accountRepository.findByName(any())).thenReturn(Optional.empty());
        when(accountRepository.save(any())).thenReturn(saved);

        AccountResponseDto result = accountService.create(command);

        Assertions.assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("my new account", result.name()),
                () -> assertEquals(new BigDecimal(0), result.balance())
        );
    }
    @Test
    void create_NameAlreadyExists_ThrowAccountAlreadyExistsException() {
        CreateAccountCommand command = new CreateAccountCommand("my new account");
        Account existingAccount = new Account(1L, "my new account", new BigDecimal(100), null);
        when(accountRepository.findByName(command.name())).thenReturn(Optional.of(existingAccount));

        AccountAlreadyExistsException exception = assertThrows(
                AccountAlreadyExistsException.class,
                () -> accountService.create(command)
        );

        assertEquals("Account with this name already exists", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }
    @Test
    void getAccount_AccountExists_ReturnsAccountResponseDto() {
        Long accountId = 1L;
        Account existingAccount = new Account(accountId, "Savings Account", new BigDecimal("1500.00"), null);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));

        AccountResponseDto result = accountService.getAccount(accountId);

        Assertions.assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("Savings Account", result.name()),
                () -> assertEquals(new BigDecimal("1500.00"), result.balance())
        );
    }
    @Test
    void getAccount_AccountDoesNotExist_ThrowsAccountNotFoundException() {
        Long nonExistingAccountId = 999L;
        when(accountRepository.findById(nonExistingAccountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccount(nonExistingAccountId)
        );

        assertEquals("Account not found", exception.getMessage());
    }
    @Test
    void delete_AccountExistsAndHasNoTransactions_DeletesAccount() {
        Long accountId = 1L;
        Account accountToDelete = new Account(accountId, "To Delete", BigDecimal.ZERO, List.of());
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountToDelete));

        accountService.delete(accountId);

        verify(accountRepository, times(1)).delete(accountToDelete);
    }
    @Test
    void delete_AccountDoesNotExist_ThrowsAccountNotFoundException() {
        Long nonExistingAccountId = 999L;
        when(accountRepository.findById(any())).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.delete(nonExistingAccountId)
        );

        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository, never()).delete(any());
    }
    @Test
    void delete_AccountHasTransactions_ThrowsAccountDeletionNotAllowed() {
        Long accountId = 1L;
        Transaction mockTransaction = new Transaction();
        Account accountWithTransactions = new Account(accountId, "Active Account", BigDecimal.TEN, List.of(mockTransaction));
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountWithTransactions));

        AccountDeletionNotAllowed exception = assertThrows(
                AccountDeletionNotAllowed.class,
                () -> accountService.delete(accountId)
        );

        assertEquals("Cannot delete account because it has transactions", exception.getMessage());
        verify(accountRepository, never()).delete(any());
    }
    @Test
    void updateBalance_AccountExists_SuccessfullyUpdatesBalanceAndSaves() {
        Long accountId = 1L;
        Account account = new Account(accountId, "Wallet", new BigDecimal("100.00"), null);
        BigDecimal amount = new BigDecimal("50.00");
        when(accountRepository.findByIdForUpdate(any())).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Account result = accountService.updateBalance(accountId, amount, TransactionType.INCOME);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(accountId, result.getId()),
                () -> assertEquals(new BigDecimal("150.00"), result.getBalance())
        );
        verify(accountRepository, times(1)).save(account);
    }
    @Test
    void updateBalance_AccountDoesNotExist_ThrowsAccountNotFoundException() {
        Long nonExistingAccountId = 999L;
        BigDecimal amount = new BigDecimal("50.00");
        when(accountRepository.findByIdForUpdate(nonExistingAccountId)).thenReturn(Optional.empty());


        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.updateBalance(nonExistingAccountId, amount, TransactionType.EXPENSE)
        );

        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }
}
