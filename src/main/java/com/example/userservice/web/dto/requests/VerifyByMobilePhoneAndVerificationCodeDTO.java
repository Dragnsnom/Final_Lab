package com.example.userservice.web.dto.requests;

import com.example.userservice.web.util.RequestValidator;
import lombok.Getter;

@Getter
public class VerifyByMobilePhoneAndVerificationCodeDTO {
   private final String mobilePhone;

   private final String verificationCode;

    public VerifyByMobilePhoneAndVerificationCodeDTO(String mobilePhone, String verificationCode) {
        this.mobilePhone = RequestValidator.mobilePhoneValidate(mobilePhone);
        this.verificationCode = RequestValidator.verificationCodeValidate(verificationCode);
    }
}
