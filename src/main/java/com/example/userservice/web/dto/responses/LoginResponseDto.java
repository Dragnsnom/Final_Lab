package com.example.userservice.web.dto.responses;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    @NonNull
    private UUID clientId;
}
