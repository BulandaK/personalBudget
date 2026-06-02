package com.example.personalBudget.model;

import com.example.personalBudget.exception.transaction.InsufficientFundsException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal balance;
    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

    public void adjustBalance(TransactionType transactionType, BigDecimal amount) {
        if(transactionType == TransactionType.INCOME) {
            deposit(amount);
        }else {
            withdraw(amount);
        }
    }
    public void withdraw(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
