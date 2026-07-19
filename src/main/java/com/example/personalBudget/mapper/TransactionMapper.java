package com.example.personalBudget.mapper;

import com.example.personalBudget.dto.transaction.TransactionResponseDto;
import com.example.personalBudget.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "accountId", source = "account.id")
    TransactionResponseDto toDto(Transaction transaction);

    @Mapping(target = "accountId", source = "transaction.account.id")
    @Mapping(target = "budgetExceeded", source = "budgetExceeded")
    @Mapping(target = "spentInCategory", source = "spentInCategory")
    TransactionResponseDto toDtoWithBudget(
            Transaction transaction,
            boolean budgetExceeded,
            BigDecimal spentInCategory
    );
}
