package com.steve.account_service.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {
    // optional: client may request an initial deposit
    private BigDecimal initialBalance;

    // optional: usually generated server-side; accept if client provides
    private String accountNumber;

    // optional/metadata
    private String accountType; // e.g. "SAVINGS", "CHECKING"
}