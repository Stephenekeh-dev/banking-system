package com.steve.account_service.service;

import com.steve.account_service.dto.*;
import com.steve.account_service.entity.Account;
import com.steve.account_service.event.TransactionEvent;
import com.steve.account_service.repository.AccountRepository;
import com.steve.account_service.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public AccountResponse createAccount(CreateAccountRequest request, String userEmail) {
        String accountNumber = request.getAccountNumber();
        if (accountNumber == null || accountNumber.isBlank()) {
            accountNumber = generateAccountNumber();
        }

        BigDecimal initial = request.getInitialBalance() != null
                ? request.getInitialBalance()
                : BigDecimal.ZERO;

        Account account = Account.builder()
                .userEmail(userEmail) // use email field
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(initial.setScale(2, RoundingMode.HALF_UP)) // money usually 2 decimals
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.save(account);

        return new AccountResponse(account);
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    public AccountResponse getAccount(String accountNumber, String userEmail) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUserEmail().equals(userEmail)) { // FIXED
            throw new RuntimeException("Unauthorized access");
        }

        return new AccountResponse(account);
    }

    public AccountResponse updateBalance(UpdateBalanceRequest request, String userEmail) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized access");
        }

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        String type;
        switch (request.getOperation().toUpperCase()) {
            case "DEPOSIT":
                account.setBalance(account.getBalance().add(amount).setScale(2, RoundingMode.HALF_UP));
                type = "DEPOSIT";
                break;

            case "WITHDRAW":
                if (account.getBalance().compareTo(amount) < 0) {
                    throw new RuntimeException("Insufficient funds");
                }
                account.setBalance(account.getBalance().subtract(amount).setScale(2, RoundingMode.HALF_UP));
                type = "WITHDRAWAL";
                break;

            default:
                throw new RuntimeException("Invalid operation. Use DEPOSIT or WITHDRAW");
        }

        accountRepository.save(account);

        // 🔹 Publish transaction event to Kafka
        TransactionEvent event = new TransactionEvent(
                account.getAccountNumber(),
                amount,
                type,
                userEmail
        );
        kafkaTemplate.send("transactions", event);

        return new AccountResponse(account);
    }

}