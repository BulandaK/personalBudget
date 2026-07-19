package com.example.personalBudget.repository;

import com.example.personalBudget.model.Category;
import com.example.personalBudget.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
                SELECT t FROM Transaction t
                WHERE (cast(:from as localdatetime) IS NULL OR t.date >= :from)
                  AND (cast(:to as localdatetime) IS NULL OR t.date <= :to)
                  AND (cast(:category as string) IS NULL OR t.category = :category)
            """)
    Page<Transaction> findWithFilters(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("category") Category category,
            Pageable pageable
    );

    @Query("""
                SELECT COALESCE(SUM(t.amount), 0)
                FROM Transaction t
                WHERE t.account.id = :accountId
                  AND t.type = 'INCOME'
                  AND t.date BETWEEN :from AND :to
            """)
    BigDecimal sumIncome(Long accountId,
                         LocalDateTime from,
                         LocalDateTime to);

    @Query("""
                SELECT COALESCE(SUM(t.amount), 0)
                FROM Transaction t
                WHERE t.account.id = :accountId
                  AND t.type = 'EXPENSE'
                  AND t.date BETWEEN :from AND :to
            """)
    BigDecimal sumExpenses(Long accountId,
                           LocalDateTime from,
                           LocalDateTime to);

    @Query("""
                SELECT t.category as category, SUM(t.amount) as amount
                FROM Transaction t
                WHERE t.account.id = :accountId
                  AND t.type = 'EXPENSE'
                  AND t.date BETWEEN :from AND :to
                GROUP BY t.category
            """)
    List<CategorySumProjection> sumExpensesByCategory(
            Long accountId,
            LocalDateTime from,
            LocalDateTime to
    );

    @Query("""
                SELECT COALESCE(SUM(t.amount), 0)
                FROM Transaction t
                WHERE t.account.id = :accountId
                  AND t.category = :category
                  AND t.type = 'EXPENSE'
                  AND t.date BETWEEN :from AND :to
            """)
    BigDecimal sumExpensesForSpecificCategory(
            Long accountId,
            Category category,
            LocalDateTime from,
            LocalDateTime to
    );

    interface CategorySumProjection {
        Category getCategory();

        BigDecimal getAmount();
    }
}
