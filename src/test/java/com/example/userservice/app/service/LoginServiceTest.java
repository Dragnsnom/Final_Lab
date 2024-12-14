package com.example.userservice.app.service;

import com.example.userservice.app.service.impl.LoginServiceImpl;
import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.model.PassportData;
import com.example.userservice.persistence.repository.ContactRepository;
import com.example.userservice.persistence.repository.PassportDataRepository;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.requests.LoginRequestDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class LoginServiceTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private PassportDataRepository passportDataRepository;

    @InjectMocks
    private LoginServiceImpl loginService;

    @Test
    void loginByPassportTest() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("login", "password", "PASSPORT_NUMBER");

        UUID idClient = UUID.randomUUID();
        Client client = new Client();
        client.setId(idClient);

        PassportData passportData = new PassportData();
        passportData.setClient(client);

        Mockito.when(passportDataRepository.findPassportDataByIdentificationPassportNumber(Mockito.anyString()))
                .thenReturn(Optional.of(passportData));
        Mockito.when(userProfileService.passwordExists(Mockito.any(), Mockito.anyString())).thenReturn(true);

        assertEquals(idClient, loginService.login(loginRequestDto));
    }

    @Test
    void loginByPhoneNumberTest() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("login", "password", "PHONE_NUMBER");

        UUID idClient = UUID.randomUUID();
        Client client = new Client();
        client.setId(idClient);

        Contact contact = new Contact();
        contact.setClient(client);

        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString()))
                .thenReturn(Optional.of(contact));
        Mockito.when(userProfileService.passwordExists(Mockito.any(), Mockito.anyString())).thenReturn(true);

        assertEquals(idClient, loginService.login(loginRequestDto));
    }

    @Test
    void invalidLoginTest() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("login", "password", "PHONE_NUMBER");

        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UnprocessableEntityException.class, () -> loginService.login(loginRequestDto));
    }

    @Test
    void invalidPasswordTest() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("login", "password", "PHONE_NUMBER");

        UUID idClient = UUID.randomUUID();
        Client client = new Client();
        client.setId(idClient);

        Contact contact = new Contact();
        contact.setClient(client);

        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString()))
                .thenReturn(Optional.of(contact));

        Mockito.when(userProfileService.passwordExists(Mockito.any(), Mockito.anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> loginService.login(loginRequestDto));
    }

}