package com.example.personalBudget.repository;

import com.example.personalBudget.model.Category;
import com.example.personalBudget.model.CategoryBudget;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget,Long> {
    Optional<CategoryBudget> findByAccountIdAndCategory( Long accountId, Category category);

    List<CategoryBudget> findByAccountId(Long accountId);
}
