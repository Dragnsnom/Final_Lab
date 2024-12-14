package com.example.userservice.app.service;

import com.example.userservice.web.dto.requests.LoginRequestDto;

import java.util.UUID;

public interface LoginService {
    UUID login(LoginRequestDto loginDataDto);
}
