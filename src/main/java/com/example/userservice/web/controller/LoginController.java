package com.example.userservice.web.controller;

import com.example.userservice.app.service.impl.LoginServiceImpl;
import com.example.userservice.web.dto.errors.ErrorResponseDTO;
import com.example.userservice.web.dto.requests.LoginRequestDto;
import com.example.userservice.web.dto.responses.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/login", consumes = "application/json", produces = "application/json")
public class LoginController {

    private final LoginServiceImpl loginService;

    @Operation(
            summary = "Разовый вход в систему",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "В случае успешного сценария (пользователь передал корректные данные и пароль)",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LoginResponseDto.class)
                            )
                    ),

                    @ApiResponse(
                            responseCode = "422",
                            description = "В случае, если по переданным данным (телефон/паспорт) клиент не был найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),

                    @ApiResponse(
                            responseCode = "400",
                            description = "В случае, если был введен не верный пароль",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginDataDto) {
        return ResponseEntity.ok(new LoginResponseDto(loginService.login(loginDataDto)));
    }
}
