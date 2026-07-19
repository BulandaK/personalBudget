package com.example.personalBudget.controller;

import com.example.personalBudget.dto.transaction.CreateTransactionCommand;
import com.example.personalBudget.dto.transaction.TransactionResponseDto;
import com.example.personalBudget.exception.account.AccountNotFoundException;
import com.example.personalBudget.exception.transaction.TransactionNotFoundException;
import com.example.personalBudget.model.Category;
import com.example.personalBudget.model.TransactionType;
import com.example.personalBudget.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;


    @Test
    void create_ValidData_Returns201AndTransactionResponseDto() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        CreateTransactionCommand command = new CreateTransactionCommand(
                new BigDecimal("250.50"), TransactionType.EXPENSE, Category.FOOD, "Lidl shopping", now, 1L
        );

        TransactionResponseDto responseDto = new TransactionResponseDto(
                45L, new BigDecimal("250.50"), TransactionType.EXPENSE, Category.FOOD, "Lidl shopping", now, 1L, false, new BigDecimal("250.50")
        );

        when(transactionService.create(any(CreateTransactionCommand.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(45L))
                .andExpect(jsonPath("$.amount").value(250.50))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.budgetExceeded").value(false))
                .andExpect(jsonPath("$.spentInCategory").value(250.50));
    }

    @Test
    void create_AccountDoesNotExist_Returns404NotFound() throws Exception {
        CreateTransactionCommand command = new CreateTransactionCommand(
                new BigDecimal("50.00"), TransactionType.EXPENSE, Category.FOOD, "Lunch", LocalDateTime.now(), 999L
        );

        when(transactionService.create(any(CreateTransactionCommand.class)))
                .thenThrow(new AccountNotFoundException("The specified account ID does not exist"));

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_InvalidData_Returns400BadRequest() throws Exception {
        CreateTransactionCommand invalidCommand = new CreateTransactionCommand(
                new BigDecimal("-50.00"), TransactionType.EXPENSE, Category.FOOD, "Invalid", LocalDateTime.now(), 1L
        );

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest());
        verify(transactionService, never()).create(any());
    }
    @Test
    void delete_TransactionExists_Returns204NoContent() throws Exception {
        Long transactionId = 45L;
        doNothing().when(transactionService).delete(transactionId);

        mockMvc.perform(delete("/api/v1/transactions/{id}", transactionId))
                .andExpect(status().isNoContent());

        verify(transactionService, times(1)).delete(transactionId);
    }

    @Test
    void delete_TransactionDoesNotExist_Returns404NotFound() throws Exception {
        Long nonExistingId = 999L;
        doThrow(new TransactionNotFoundException("Transaction not found")).when(transactionService).delete(nonExistingId);

        mockMvc.perform(delete("/api/v1/transactions/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }
}