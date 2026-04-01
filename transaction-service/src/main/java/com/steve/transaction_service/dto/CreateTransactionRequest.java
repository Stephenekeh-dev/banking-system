package com.steve.transaction_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    private String accountNumber;
    private BigDecimal amount;
    private String type;           // "DEPOSIT", "WITHDRAWAL", "TRANSFER"
    private String targetAccount;  // required only for TRANSFER
}