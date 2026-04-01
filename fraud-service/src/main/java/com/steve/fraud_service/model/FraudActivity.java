package com.steve.fraud_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_activity")
public class FraudActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;  // ID of the transaction being flagged
    private String userId;         // User who performed the transaction
    private String reason;         // Reason for suspicion
    private double amount;         // Transaction amount
    private LocalDateTime timestamp; // When the suspicious activity occurred

    // Constructors
    public FraudActivity() {}

    public FraudActivity(String transactionId, String userId, String reason, double amount, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.reason = reason;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}