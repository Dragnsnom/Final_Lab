package com.example.userservice.web.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthorizationTypeIncomingDto {
    private boolean logpass;
    private boolean pincode;
    private boolean biometrics;
}
