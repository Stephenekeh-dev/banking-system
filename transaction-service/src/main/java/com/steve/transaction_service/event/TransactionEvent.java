package com.steve.transaction_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String accountNumber;
    private BigDecimal amount;
    private String type;
    private String userEmail;
    private String targetAccount;
}