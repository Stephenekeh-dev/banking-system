package com.steve.transaction_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;   // account linked
    private String userEmail;       // owner of the transaction

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;   // DEPOSIT, WITHDRAWAL, TRANSFER

    private String targetAccount;   // for transfers

    private LocalDateTime createdAt;
}