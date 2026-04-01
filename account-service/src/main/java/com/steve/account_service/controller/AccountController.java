package com.steve.account_service.controller;

import com.steve.account_service.dto.*;
import com.steve.account_service.service.AccountService;
import com.steve.account_service.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request,
                                         Authentication auth) {
        // attach user email from JWT
        return accountService.createAccount(request, auth.getName());
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getAccount(@PathVariable String accountNumber,
                                      Authentication auth) {
        // optional: enforce that user only accesses own accounts
        return accountService.getAccount(accountNumber, auth.getName());
    }

    @PostMapping("/update-balance")
    public AccountResponse updateBalance(@RequestBody UpdateBalanceRequest request,
                                         Authentication auth) {
        return accountService.updateBalance(request, auth.getName());
    }
}
