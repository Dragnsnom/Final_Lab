package com.example.userservice.web.dto.requests;

import com.example.userservice.web.util.annotation.Password;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class LoginRequestDto {

    @NonNull
    private String login;

    @NonNull
    @Password
    private String password;

    @NonNull
    private String type;
}
