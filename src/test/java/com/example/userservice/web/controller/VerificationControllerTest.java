package com.example.userservice.web.controller;

import com.example.userservice.app.service.impl.VerificationServiceImpl;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.requests.VerifyByMobilePhoneAndVerificationCodeDTO;
import com.example.userservice.web.dto.responses.*;
import com.example.userservice.web.dto.requests.VerifyByPassportNumberAndVerificationCodeDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class VerificationControllerTest {

    @Mock
    private VerificationServiceImpl verificationService;

    @InjectMocks
    private VerificationController verificationController;

    @Test
    void checkVerifyByValidMobilePhoneAndVerificationCode() {
        ResponseEntity<HttpStatus> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        assertEquals(responseEntity, verificationController.verifyByMobilePhoneAndVerificationCode(
                new VerifyByMobilePhoneAndVerificationCodeDTO("79370458234", "234756"))
        );
    }

    @Test
    void checkVerifyByInvalidMobilePhoneAndVerificationCode() {
        doThrow(UnprocessableEntityException.class).when(verificationService)
                .verifyByMobilePhoneAndVerificationCode(Mockito.anyString(), Mockito.anyString());

        assertThrows(UnprocessableEntityException.class, () -> verificationController.verifyByMobilePhoneAndVerificationCode(
                new VerifyByMobilePhoneAndVerificationCodeDTO("79370458234", "234756"))
        );
    }

    @Test
    void checkVerifyByValidMobilePhoneAndVerificationCodeWithAuth() {
        AuthResponseDto authResponseDto = new AuthResponseDto(UUID.randomUUID());

        Mockito.when(verificationService.verifyByMobilePhoneAndVerificationCodeWithAuth(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(authResponseDto);

        ResponseEntity<AuthResponseDto> responseEntity = new ResponseEntity<>(authResponseDto, HttpStatus.OK);

        assertEquals(responseEntity, verificationController.verifyByMobilePhoneAndVerificationCodeWithAuth(
                new VerifyByMobilePhoneAndVerificationCodeDTO("79370458234", "234756"))
        );
    }

    @Test
    void checkVerifyByInvalidMobilePhoneAndVerificationCodeWithAuth() {
        Mockito.when(verificationService.verifyByMobilePhoneAndVerificationCodeWithAuth(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(UnprocessableEntityException.class);

        assertThrows(UnprocessableEntityException.class, () -> verificationController.verifyByMobilePhoneAndVerificationCodeWithAuth(
                new VerifyByMobilePhoneAndVerificationCodeDTO("79370458234", "234756"))
        );
    }

    @Test
    void generateCodeByMobilePhoneWithValidParameterTest() {
        String mobilePhone = "79345612385";
        doNothing().when(verificationService).generateCodeByMobilePhone(mobilePhone);

        MobilePhoneDTO mobilePhoneDto = new MobilePhoneDTO("");
        mobilePhoneDto.setMobilePhone(mobilePhone);
        ResponseEntity<HttpStatus> responseEntity = verificationController.generateCodeByMobilePhone(mobilePhoneDto);

        assertSame(responseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    void generateCodeByPassportDataWithValidParameterTest() {
        String passportNumber = "AB3427798";
        doNothing().when(verificationService).generateCodeByPassportNumber(passportNumber);

        PassportNumberDto passportNumberDto = new PassportNumberDto("");
        passportNumberDto.setPassportNumber(passportNumber);
        ResponseEntity<HttpStatus> responseEntity = verificationController.generateCodeByPassportNumber(passportNumberDto);

        assertSame(responseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    void VerifyByPassportNumberAndVerificationCodeWithAuth() {
        AuthResponseDto authResponseDto = new AuthResponseDto(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));

        Mockito.when(verificationService.verifyByPassportNumberAndVerificationCodeWithAuth(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(authResponseDto);

        ResponseEntity<AuthResponseDto> responseEntity = new ResponseEntity<>(authResponseDto, HttpStatus.OK);

        assertEquals(responseEntity, verificationController.verifyByPassportNumberAndVerificationCodeWithAuth(
                new VerifyByPassportNumberAndVerificationCodeDTO("23440123M", "234756"))
        );
    }

    @Test
    void VerifyByIncorrectPassportNumberAndVerificationCodeWithAuth() {
        Mockito.when(verificationService.verifyByPassportNumberAndVerificationCodeWithAuth(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(UnprocessableEntityException.class);

        assertThrows(UnprocessableEntityException.class, () -> verificationController.verifyByPassportNumberAndVerificationCodeWithAuth(
                new VerifyByPassportNumberAndVerificationCodeDTO("23440123M", "234756"))
        );
    }
}