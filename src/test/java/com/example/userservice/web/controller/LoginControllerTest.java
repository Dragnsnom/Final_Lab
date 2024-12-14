package com.example.userservice.web.controller;

import com.example.userservice.app.service.impl.LoginServiceImpl;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.requests.LoginRequestDto;
import com.example.userservice.web.dto.responses.LoginResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    private static final String URL_LOGIN = "/api/v1/login";

    private static LoginRequestDto loginRequestDto;
    private static LoginResponseDto loginResponseDto;

    @MockBean
    private LoginServiceImpl loginService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp(){
        loginRequestDto = new LoginRequestDto("login", "pass", "type");
        loginResponseDto = new LoginResponseDto(UUID.randomUUID());
    }

    @Test
    void login_whenDataCorrect_thenReturnClientId() throws Exception {
        Mockito.when(loginService.login(Mockito.any())).thenReturn(loginResponseDto.getClientId());
        mockMvc.perform(post(URL_LOGIN)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(loginResponseDto.getClientId().toString()));

        Mockito.verify(loginService, Mockito.only()).login(Mockito.any());
    }

    @Test
    void login_whenDataIncorrect_thenReturnStatusUnprocessable() throws Exception {
        Mockito.when(loginService.login(Mockito.any())).thenThrow(UnprocessableEntityException.class);
        mockMvc.perform(post(URL_LOGIN)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnprocessableEntity());

        Mockito.verify(loginService, Mockito.only()).login(Mockito.any());
    }

    @Test
    void login_whenPasswordIncorrect_thenReturnStatusBadRequest() throws Exception {
        Mockito.when(loginService.login(Mockito.any())).thenThrow(BadRequestException.class);
        mockMvc.perform(post(URL_LOGIN)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(loginService, Mockito.only()).login(Mockito.any());
    }
}