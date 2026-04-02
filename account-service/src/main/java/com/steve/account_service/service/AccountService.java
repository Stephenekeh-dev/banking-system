package com.steve.account_service.service;



import com.steve.account_service.dto.AccountResponse;
import com.steve.account_service.dto.CreateAccountRequest;
import com.steve.account_service.dto.UpdateBalanceRequest;
import com.steve.account_service.entity.Account;
import com.steve.account_service.event.TransactionEvent;
import com.steve.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, String userEmail) {
        String accountNumber = (request.getAccountNumber() != null && !request.getAccountNumber().isBlank())
                ? request.getAccountNumber()
                : generateAccountNumber();

        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("Account number already exists: " + accountNumber);
        }

        BigDecimal initialBalance = request.getInitialBalance() != null
                ? request.getInitialBalance().setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2);

        Account account = Account.builder()
                .userEmail(userEmail)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType().toUpperCase())
                .balance(initialBalance)
                .build();

        accountRepository.save(account);
        log.info("Account created: {} for user: {}", accountNumber, userEmail);
        return new AccountResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountNumber, String userEmail) {
        Account account = findAndVerifyOwnership(accountNumber, userEmail);
        return new AccountResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUser(String userEmail) {
        return accountRepository.findByUserEmail(userEmail)
                .stream()
                .map(AccountResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse updateBalance(UpdateBalanceRequest request, String userEmail) {
        Account account = findAndVerifyOwnership(request.getAccountNumber(), userEmail);

        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        String operation = request.getOperation().toUpperCase();
        String eventType;

        switch (operation) {
            case "DEPOSIT" -> {
                account.setBalance(account.getBalance().add(amount));
                eventType = "DEPOSIT";
            }
            case "WITHDRAW" -> {
                if (account.getBalance().compareTo(amount) < 0) {
                    throw new IllegalStateException("Insufficient funds. Available: " + account.getBalance());
                }
                account.setBalance(account.getBalance().subtract(amount));
                eventType = "WITHDRAWAL";
            }
            default -> throw new IllegalArgumentException("Invalid operation: " + operation + ". Use DEPOSIT or WITHDRAW.");
        }

        accountRepository.save(account);

        // Publish to Kafka — transaction-service, audit-service, notification-service all listen
        TransactionEvent event = TransactionEvent.builder()
                .accountNumber(account.getAccountNumber())
                .amount(amount)
                .type(eventType)
                .userEmail(userEmail)
                .build();
        kafkaTemplate.send("transactions", event);
        log.info("Published transaction event: {} {} for account {}", eventType, amount, account.getAccountNumber());

        return new AccountResponse(account);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private Account findAndVerifyOwnership(String accountNumber, String userEmail) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + accountNumber));

        if (!account.getUserEmail().equals(userEmail)) {
            throw new SecurityException("Access denied: you do not own this account");
        }
        return account;
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}