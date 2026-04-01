package com.steve.transaction_service.dto;

import com.steve.transaction_service.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private String accountNumber;
    private String userEmail;
    private BigDecimal amount;
    private String type;
    private String targetAccount;
    private LocalDateTime createdAt;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.accountNumber = transaction.getAccountNumber();
        this.userEmail = transaction.getUserEmail();
        this.amount = transaction.getAmount();
        this.type = transaction.getType().name();
        this.targetAccount = transaction.getTargetAccount();
        this.createdAt = transaction.getCreatedAt();
    }
}