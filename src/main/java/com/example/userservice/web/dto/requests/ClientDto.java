package com.example.userservice.web.dto.requests;

import com.example.userservice.web.util.annotation.Password;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ClientDto {
    @NotNull
    private UUID id;
    @NotNull
    private String mobilePhone;
    @NotNull
    private String email;
    @NotNull
    @Password
    private String password;
    @NotNull
    private String securityQuestion;
    @NotNull
    private String securityAnswer;
}
