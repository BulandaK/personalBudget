package com.example.personalBudget.controller;

import com.example.personalBudget.dto.account.AccountResponseDto;
import com.example.personalBudget.dto.account.CreateAccountCommand;
import com.example.personalBudget.exception.account.AccountNotFoundException;
import com.example.personalBudget.exception.account.AccountDeletionNotAllowed;
import com.example.personalBudget.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AccountService accountService;

    @Test
    void create_ValidData_Returns201AndAccountResponseDto() throws Exception {
        CreateAccountCommand command = new CreateAccountCommand("New Wallet");
        AccountResponseDto responseDto = new AccountResponseDto(1L, "New Wallet", BigDecimal.ZERO);

        when(accountService.create(any(CreateAccountCommand.class))).thenReturn(responseDto);


        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Wallet"))
                .andExpect(jsonPath("$.balance").value(0));
    }
    @Test
    void create_InvalidData_Returns400BadRequest() throws Exception {
        CreateAccountCommand invalidCommand = new CreateAccountCommand("");

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).create(any());
    }
    @Test
    void deleteAccount_AccountExistsAndHasNoTransactions_Returns204NoContent() throws Exception {
        Long accountId = 1L;
        doNothing().when(accountService).delete(accountId);

        mockMvc.perform(delete("/api/v1/accounts/{id}", accountId))
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).delete(accountId);
    }
    @Test
    void deleteAccount_AccountDoesNotExist_Returns404NotFound() throws Exception {
        Long nonExistingId = 999L;
        doThrow(new AccountNotFoundException("Account not found")).when(accountService).delete(nonExistingId);

        mockMvc.perform(delete("/api/v1/accounts/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteAccount_AccountHasTransactions_Returns409Conflict() throws Exception {
        Long accountId = 1L;
        doThrow(new AccountDeletionNotAllowed("Cannot delete account")).when(accountService).delete(accountId);

        mockMvc.perform(delete("/api/v1/accounts/{id}", accountId))
                .andExpect(status().isConflict());
    }
}
