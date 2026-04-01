package com.steve.approval_service.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest {
    private UUID transactionId;
    private Double amount;   // optional: transaction amount
    private UUID accountId;  // optional: account reference
}