package com.steve.account_service;

import com.steve.account_service.dto.CreateAccountRequest;
import com.steve.account_service.dto.UpdateBalanceRequest;
import com.steve.account_service.dto.AccountResponse;
import com.steve.account_service.entity.Account;
import com.steve.account_service.event.TransactionEvent;
import com.steve.account_service.repository.AccountRepository;
import com.steve.account_service.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @InjectMocks private AccountService accountService;

    private final String USER_EMAIL = "test@example.com";
    private final String ACCOUNT_NUMBER = "ABC123456789";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ── createAccount ─────────────────────────────────────────────────────────

    @Test
    void createAccount_generatesAccountNumber_whenNotProvided() {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .accountType("SAVINGS")
                .initialBalance(BigDecimal.valueOf(500))
                .build();

        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AccountResponse response = accountService.createAccount(request, USER_EMAIL);

        assertThat(response.getAccountNumber()).isNotBlank();
        assertThat(response.getBalance()).isEqualByComparingTo("500.00");
        assertThat(response.getEmail()).isEqualTo(USER_EMAIL);
    }

    @Test
    void createAccount_usesProvidedAccountNumber() {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .accountType("CHECKING")
                .accountNumber(ACCOUNT_NUMBER)
                .initialBalance(BigDecimal.ZERO)
                .build();

        when(accountRepository.existsByAccountNumber(ACCOUNT_NUMBER)).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AccountResponse response = accountService.createAccount(request, USER_EMAIL);

        assertThat(response.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
    }

    @Test
    void createAccount_throwsWhenAccountNumberAlreadyExists() {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .accountType("SAVINGS")
                .accountNumber(ACCOUNT_NUMBER)
                .build();

        when(accountRepository.existsByAccountNumber(ACCOUNT_NUMBER)).thenReturn(true);

        assertThatThrownBy(() -> accountService.createAccount(request, USER_EMAIL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    // ── getAccount ────────────────────────────────────────────────────────────

    @Test
    void getAccount_returnsAccount_whenOwner() {
        Account account = buildAccount();
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccount(ACCOUNT_NUMBER, USER_EMAIL);

        assertThat(response.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
    }

    @Test
    void getAccount_throwsSecurityException_whenNotOwner() {
        Account account = buildAccount();
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.getAccount(ACCOUNT_NUMBER, "other@example.com"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void getAccount_throwsNotFound_whenAccountMissing() {
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccount(ACCOUNT_NUMBER, USER_EMAIL))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── updateBalance / DEPOSIT ───────────────────────────────────────────────

    @Test
    void updateBalance_deposit_addsAmount() {
        Account account = buildAccount(); // balance = 1000
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UpdateBalanceRequest request = new UpdateBalanceRequest(ACCOUNT_NUMBER, BigDecimal.valueOf(200), "DEPOSIT");
        AccountResponse response = accountService.updateBalance(request, USER_EMAIL);

        assertThat(response.getBalance()).isEqualByComparingTo("1200.00");
        verify(kafkaTemplate).send(eq("transactions"), any(TransactionEvent.class));
    }

    // ── updateBalance / WITHDRAW ──────────────────────────────────────────────

    @Test
    void updateBalance_withdraw_subtractsAmount() {
        Account account = buildAccount(); // balance = 1000
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UpdateBalanceRequest request = new UpdateBalanceRequest(ACCOUNT_NUMBER, BigDecimal.valueOf(300), "WITHDRAW");
        AccountResponse response = accountService.updateBalance(request, USER_EMAIL);

        assertThat(response.getBalance()).isEqualByComparingTo("700.00");
    }

    @Test
    void updateBalance_withdraw_throwsOnInsufficientFunds() {
        Account account = buildAccount(); // balance = 1000
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        UpdateBalanceRequest request = new UpdateBalanceRequest(ACCOUNT_NUMBER, BigDecimal.valueOf(5000), "WITHDRAW");

        assertThatThrownBy(() -> accountService.updateBalance(request, USER_EMAIL))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient funds");
    }

    @Test
    void updateBalance_throwsOnInvalidOperation() {
        Account account = buildAccount();
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        UpdateBalanceRequest request = new UpdateBalanceRequest(ACCOUNT_NUMBER, BigDecimal.TEN, "TRANSFER");

        assertThatThrownBy(() -> accountService.updateBalance(request, USER_EMAIL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid operation");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Account buildAccount() {
        return Account.builder()
                .id(1L)
                .userEmail(USER_EMAIL)
                .accountNumber(ACCOUNT_NUMBER)
                .accountType("SAVINGS")
                .balance(BigDecimal.valueOf(1000.00))
                .build();
    }
}