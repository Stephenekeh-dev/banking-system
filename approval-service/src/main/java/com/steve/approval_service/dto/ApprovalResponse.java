package com.steve.approval_service.dto;

import com.steve.approval_service.model.ApprovalStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalResponse {
    private UUID id;
    private UUID transactionId;
    private ApprovalStatus status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}