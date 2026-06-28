package com.example.usersaccountservice.service;

import com.example.usersaccountservice.dto.ApiResponse;
import com.example.usersaccountservice.dto.UserDTO;
import com.example.usersaccountservice.dto.UserStatisticsDTO;
import com.example.usersaccountservice.dto.UserWithAccountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    ApiResponse<UserWithAccountDTO> getMyDetails();

    ApiResponse<UserWithAccountDTO> searchUser(String email, String accountNumber);

    ApiResponse<Page<UserDTO>> getAllUser(String roleName, Pageable pageable);

    ApiResponse<UserStatisticsDTO> getUserStatistics();

    ApiResponse<String> toggleUserStatus(Long userId);
}