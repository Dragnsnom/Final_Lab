package com.example.userservice.web.dto.requests;

import com.example.userservice.web.util.RequestValidator;
import lombok.Getter;

@Getter
public class VerifyByPassportNumberAndVerificationCodeDTO {

    private final String passportNumber;

    private final String verificationCode;

    public VerifyByPassportNumberAndVerificationCodeDTO(String passportNumber, String verificationCode) {
        this.passportNumber = RequestValidator.passportNumberValidate(passportNumber);
        this.verificationCode = RequestValidator.verificationCodeValidate(verificationCode);
    }

}
