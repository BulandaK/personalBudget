package com.example.personalBudget.mapper;

import com.example.personalBudget.dto.account.AccountResponseDto;
import com.example.personalBudget.model.Account;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    List<AccountResponseDto> toDtoList(List<Account> list);
    AccountResponseDto toDto(Account account);
}
