package com.example.usersaccountservice.service;

import com.example.usersaccountservice.dto.ApiResponse;
import com.example.usersaccountservice.dto.AuthResponse;
import com.example.usersaccountservice.dto.LoginRequest;
import com.example.usersaccountservice.dto.RegistrationRequest;

public interface AuthService {

    ApiResponse<AuthResponse> registerUser(RegistrationRequest registrationRequest);
    ApiResponse<AuthResponse> loginRequest(LoginRequest loginRequest);

    ApiResponse<AuthResponse> loginUser(LoginRequest loginRequest);
}
