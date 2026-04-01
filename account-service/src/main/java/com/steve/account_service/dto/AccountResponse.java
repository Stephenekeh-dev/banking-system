package com.steve.account_service.dto;

import com.steve.account_service.entity.Account;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String email;
    private LocalDateTime createdAt;

    public AccountResponse(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.accountType = account.getAccountType();
        this.balance = account.getBalance();
        this.email = account.getUserEmail();
        this.createdAt = account.getCreatedAt();
    }
}
