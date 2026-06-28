package com.example.usersaccountservice.service;

import com.example.usersaccountservice.dto.AccountDTO;
import com.example.usersaccountservice.dto.ApiResponse;
import com.example.usersaccountservice.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

    ApiResponse<AccountDTO> getMyAccount();

    ApiResponse<AccountDTO> getAccountNumber(String accountNumber);

    ApiResponse<AccountDTO> changeAccountStatus(String accountNumber, AccountStatus status);

    ApiResponse<Page<AccountDTO>> getAllAccounts(Pageable pageable);
}