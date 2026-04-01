package com.steve.account_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private String accountNumber;
    private BigDecimal amount;
    private String type;       // "DEPOSIT" or "WITHDRAWAL"
    private String userEmail;
}