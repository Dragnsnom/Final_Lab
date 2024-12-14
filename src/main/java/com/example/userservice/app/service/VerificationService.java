package com.example.userservice.app.service;

import com.example.userservice.persistence.model.Verification;
import com.example.userservice.web.dto.responses.AuthResponseDto;

import java.util.List;
import java.util.Optional;


public interface VerificationService {

    Optional<Verification> findById(String mobilePhone);

    List<Verification> findAll();

    void deleteById(String mobilePhone);

    Verification save(Verification entity);

    void generateCodeByMobilePhone(String mobilePhone);

    void generateCodeByPassportNumber(String passportNumber);

    void verifyByMobilePhoneAndVerificationCode(String mobilePhone, String verificationCode);

    AuthResponseDto verifyByMobilePhoneAndVerificationCodeWithAuth(String mobilePhone, String verificationCode);

    AuthResponseDto verifyByPassportNumberAndVerificationCodeWithAuth(String passportNumber, String verificationCode);

}

