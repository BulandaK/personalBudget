package com.example.personalBudget.service;

import com.example.personalBudget.dto.categoryBudget.CategoryBudgetResponseDto;
import com.example.personalBudget.dto.categoryBudget.CategoryBudgetUpdateCommand;
import com.example.personalBudget.dto.categoryBudget.CreateCategoryBudgetCreateCommand;
import com.example.personalBudget.exception.account.AccountNotFoundException;
import com.example.personalBudget.exception.categoryBudget.BudgetNotFoundException;
import com.example.personalBudget.mapper.CategoryBudgetMapper;
import com.example.personalBudget.model.Account;
import com.example.personalBudget.model.Category;
import com.example.personalBudget.model.CategoryBudget;
import com.example.personalBudget.model.TransactionType;
import com.example.personalBudget.repository.AccountRepository;
import com.example.personalBudget.repository.CategoryBudgetRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryBudgetService {
    private final CategoryBudgetRepository categoryBudgetRepository;
    private final AccountRepository accountRepository;
    private final CategoryBudgetMapper categoryBudgetMapper;

    public CategoryBudgetResponseDto setLimit(CreateCategoryBudgetCreateCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        CategoryBudget budget = categoryBudgetRepository
                .findByAccountIdAndCategory(command.accountId(), command.category())
                .orElse(new CategoryBudget());

        budget.setAccount(account);
        budget.setCategory(command.category());
        budget.setLimitAmount(command.limitAmount());

        CategoryBudget saved = categoryBudgetRepository.save(budget);

        return categoryBudgetMapper.toDto(saved);
    }

    public List<CategoryBudgetResponseDto> getByAccount(Long accountId) {
        return categoryBudgetRepository.findByAccountId(accountId)
                .stream()
                .map(categoryBudgetMapper::toDto)
                .toList();
    }

    public CategoryBudgetResponseDto updateLimit(@Valid CategoryBudgetUpdateCommand command) {
        CategoryBudget budget = categoryBudgetRepository
                .findByAccountIdAndCategory(command.accountId(), command.category())
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        budget.setLimitAmount(command.limitAmount());

        return categoryBudgetMapper.toDto(categoryBudgetRepository.save(budget));
    }

    public void delete(Long id) {
        CategoryBudget budget = categoryBudgetRepository
                .findById(id)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        categoryBudgetRepository.delete(budget);
    }
    public boolean isBudgetExceeded(Long accountId, Category category, TransactionType type, BigDecimal spentAmount) {
        if (type != TransactionType.EXPENSE) {
            return false;
        }

        return categoryBudgetRepository.findByAccountIdAndCategory(accountId, category)
                .map(budget -> spentAmount.compareTo(budget.getLimitAmount()) > 0)
                .orElse(false);
    }
}
