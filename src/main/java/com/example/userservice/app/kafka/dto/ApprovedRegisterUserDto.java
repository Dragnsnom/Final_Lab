package com.example.userservice.app.kafka.dto;

import com.example.userservice.app.kafka.dto.enums.Approval;
import com.example.userservice.app.kafka.dto.enums.ClientFlow;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Schema(description = "dto с результатом подтверждения регистрации клиента")
public class ApprovedRegisterUserDto {

    @Schema(description = "id клиента")
    private UUID clientId;

    @Schema(description = "результат подтверждения регистрации", examples ={"ALLOWED","BLOCKED"})
    private Approval approval;

    @Schema(description = "информация является ли пользователь клиентом банка", example = "OLD / NEW")
    private ClientFlow clientFlow;
}
