package com.steve.account_service.dto;

import lombok.*;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceRequest {
    private String accountNumber;
    private BigDecimal amount;
    private String operation; // "DEPOSIT" or "WITHDRAW"
}