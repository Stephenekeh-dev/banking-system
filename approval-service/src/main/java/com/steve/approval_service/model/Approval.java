package com.steve.approval_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "approvals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Approval {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID transactionId;  // Reference to transaction-service record

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status; // APPROVED / REJECTED / PENDING

    private String reason; // e.g., "Insufficient balance"

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}