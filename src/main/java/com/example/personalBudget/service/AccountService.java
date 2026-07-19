package com.example.personalBudget.service;

import com.example.personalBudget.dto.account.AccountResponseDto;
import com.example.personalBudget.dto.account.CreateAccountCommand;
import com.example.personalBudget.exception.account.AccountAlreadyExistsException;
import com.example.personalBudget.exception.account.AccountDeletionNotAllowed;
import com.example.personalBudget.exception.account.AccountNotFoundException;
import com.example.personalBudget.mapper.AccountMapper;
import com.example.personalBudget.model.Account;
import com.example.personalBudget.model.TransactionType;
import com.example.personalBudget.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public List<AccountResponseDto> getAll() {
        List<Account> allAccounts = accountRepository.findAll();
        return accountMapper.toDtoList(allAccounts);
    }

    @Transactional
    public AccountResponseDto create(CreateAccountCommand command) {
        checkIfAccountExistsByName(command.name());
        Account saved = accountRepository.save(new Account(null, command.name(), new BigDecimal(0),null));
        return accountMapper.toDto(saved);
    }

    public AccountResponseDto getAccount(Long id) {
        Account account = getAccountOrThrowException(id);
        return accountMapper.toDto(account);
    }

    @Transactional
    public void delete(Long id) {
        Account account = getAccountOrThrowException(id);
        checkIfAccountHasTransactions(account);
        accountRepository.delete(account);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Account updateBalance(Long accountId, BigDecimal amount, TransactionType transactionType) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        account.adjustBalance(transactionType,amount);
        accountRepository.save(account);
        return account;
    }

    private void checkIfAccountExistsByName(String name) {
        accountRepository.findByName(name)
                .ifPresent(account -> {
                    throw new AccountAlreadyExistsException("Account with this name already exists");
                });
    }

    public Account getAccountOrThrowException(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    private void checkIfAccountHasTransactions(Account account) {
        if(!account.getTransactions().isEmpty()) {
            throw new AccountDeletionNotAllowed("Cannot delete account because it has transactions");
        }
    }
}
