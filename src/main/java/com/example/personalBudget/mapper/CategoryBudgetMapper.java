package com.example.personalBudget.mapper;

import com.example.personalBudget.dto.categoryBudget.CategoryBudgetResponseDto;
import com.example.personalBudget.model.CategoryBudget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryBudgetMapper {
    @Mapping(target = "accountId", source = "account.id")
    CategoryBudgetResponseDto toDto(CategoryBudget categoryBudget);
}
