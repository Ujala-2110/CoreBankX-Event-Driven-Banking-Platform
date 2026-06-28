package com.example.usersaccountservice.service.impl;


import com.example.usersaccountservice.dto.*;
import com.example.usersaccountservice.entity.Account;
import com.example.usersaccountservice.entity.Role;
import com.example.usersaccountservice.entity.User;
import com.example.usersaccountservice.enums.AccountStatus;
import com.example.usersaccountservice.enums.AccountType;
import com.example.usersaccountservice.enums.Currency;
import com.example.usersaccountservice.exceptions.BadRequestException;
import com.example.usersaccountservice.exceptions.NotFoundException;
import com.example.usersaccountservice.kafka.dto.UserRegistrationEvent;
import com.example.usersaccountservice.kafka.service.AccountEventPublisher;
import com.example.usersaccountservice.repository.AccountRepository;
import com.example.usersaccountservice.repository.RoleRepository;
import com.example.usersaccountservice.repository.UserRepository;
import com.example.usersaccountservice.security.JwtService;
import com.example.usersaccountservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final AccountEventPublisher accountEventPublisher;


    @Override
    public ApiResponse<AuthResponse> registerUser(RegistrationRequest registrationRequest) {
        log.info("We are inside the register user service method");
        if(userRepository.existsByEmail(registrationRequest.getEmail())){
            throw new BadRequestException("Account already exist for this email");
        }

        Set<Role> roles = new HashSet<>();

        String roleName = (registrationRequest.getRole() != null && !registrationRequest.getRole().isBlank())
                ? registrationRequest.getRole().toUpperCase()
                : "CUSTOMER";

        Role databaseRole = roleRepository.findByName(roleName)
                .orElseThrow(()-> new NotFoundException("Role with name" + roleName + " Not found"));

        roles.add(databaseRole);

        User userToSave = User.builder()
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .enabled(true)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(userToSave);

        //generate a unique account number for the uer
        String accountNumber = generateUniqueAccountNumber();

        Account accountToSaveToDb = Account.builder()
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .currency(Currency.USD)
                .accountType(AccountType.SAVINGS)
                .accountStatus(AccountStatus.ACTIVE)
                .user(savedUser)
                .build();

        accountRepository.save(accountToSaveToDb);

        //Publish event out to the notification service
        UserRegistrationEvent userRegistrationEvent = UserRegistrationEvent.builder()
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .accountNumber(accountNumber)
                .bankName("NOVA BANK")
                .build();

        accountEventPublisher.publishedUserRegistrationEvent(userRegistrationEvent);


        //convert to dto
        UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);

        AuthResponse authResponse = AuthResponse.builder()
                .user(userDTO)
                .build();

        return new ApiResponse<>(HttpStatus.CONTINUE.value(), "User account created successfully", authResponse);


    }

    @Override
    public ApiResponse<AuthResponse> loginRequest(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public ApiResponse<AuthResponse> loginUser(LoginRequest loginRequest) {
        log.info("Inside login service method");
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new NotFoundException("User not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadRequestException("Password doesn't match");
        }
        if(!user.isEnabled()){
            throw new BadRequestException("User is disabled");
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String token = jwtService.generateToken(user.getEmail(), roles);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "login successful",
                authResponse
        );

    }




    private String generateUniqueAccountNumber(){
        String accountNumber;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        do{
            //it will generate random 8 digits(between 0000000 to 99999999)
            int randomPart = random.nextInt(100_000_000);
            accountNumber = String.format("00%08d", randomPart);

        }while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}
























