package com.example.userservice.app.kafka.dto;

import com.example.userservice.app.kafka.dto.enums.ClientFlow;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Schema(description = "dto с данными клиента для подтверждения регистрации")
public class RegisterUserDto {
    @Schema(description = "id клиента")
    private UUID clientId;

    @Schema(description = "email клиента", example = "fvpadct@yandex.ru")
    private String email;

    @Schema(description = "информация является ли пользователь клиентом банка", example = "OLD / NEW")
    private ClientFlow clientFlow;
}
