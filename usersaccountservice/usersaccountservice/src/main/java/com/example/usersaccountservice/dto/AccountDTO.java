package com.example.usersaccountservice.dto;

import com.example.usersaccountservice.enums.AccountStatus;
import com.example.usersaccountservice.enums.AccountType;
import com.example.usersaccountservice.enums.Currency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {

    private Long id;

    private String accountNumber;

    private BigDecimal balance;

    private Currency currency; // USD, EURO

    private AccountType accountType; //SAVINGS, CURRENT

    private AccountStatus accountStatus; //ACTIVE, CLOSED, FROZEN

    private String ownerEmail;

    private LocalDateTime createdAt;
}







