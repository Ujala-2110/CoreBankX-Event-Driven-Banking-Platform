package com.example.usersaccountservice.controller;


import com.example.usersaccountservice.dto.AccountDTO;
import com.example.usersaccountservice.dto.ApiResponse;
import com.example.usersaccountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountDTO>> getMyAccount() {
        return ResponseEntity.ok(accountService.getMyAccount());
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountByNumber(
            @PathVariable String accountNumber
    ) {
        return ResponseEntity.ok(accountService.getAccountNumber(accountNumber));
    }
}








