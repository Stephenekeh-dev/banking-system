package com.steve.account_service.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;     // (optional) or remove if using email only
    private String userEmail;   // keep if you use email to link to auth-service

    @Column(unique = true, nullable = false)
    private String accountNumber;

    private String accountType;

    @Column(precision = 19, scale = 4)
    private BigDecimal balance;

    private LocalDateTime createdAt;
}
