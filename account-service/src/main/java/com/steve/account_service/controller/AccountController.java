package com.steve.account_service.controller;
import com.steve.account_service.dto.AccountResponse;
import com.steve.account_service.dto.CreateAccountRequest;
import com.steve.account_service.dto.UpdateBalanceRequest;
import com.steve.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(request, auth.getName()));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable String accountNumber,
            Authentication auth) {
        return ResponseEntity.ok(accountService.getAccount(accountNumber, auth.getName()));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getUserAccounts(Authentication auth) {
        return ResponseEntity.ok(accountService.getAccountsByUser(auth.getName()));
    }

    @PostMapping("/update-balance")
    public ResponseEntity<AccountResponse> updateBalance(
            @Valid @RequestBody UpdateBalanceRequest request,
            Authentication auth) {
        return ResponseEntity.ok(accountService.updateBalance(request, auth.getName()));
    }

    // ── Exception Handlers ────────────────────────────────────────────────────

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }
}