package com.example.userservice.app.service;


import com.example.userservice.app.service.impl.ContactServiceImpl;
import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.model.PassportData;
import com.example.userservice.persistence.repository.ContactRepository;
import com.example.userservice.persistence.repository.PassportDataRepository;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.DuplicateException;
import com.example.userservice.web.controller.exception.NotFoundException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.requests.EmailAndPassportDto;
import com.example.userservice.web.dto.responses.NotificationsInfoDto;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class ContactServiceImplTest {
    private static final String EMAIL = "batman@yandex.ru";
    private static final String PASSPORT = "SA95949";
    private static final UUID ID = UUID.randomUUID();
    private final Contact contact = new Contact();
    private final PassportData passportData = new PassportData();

    @Mock
    private ContactRepository repository;
    @Mock
    Logger logger;
    @Mock
    private PassportDataRepository passportDataRepository;
    @InjectMocks
    private ContactServiceImpl service;


    @Test
    void changeEmail_whenAllParamsOk_thenSavingInRepository() {
        when(repository.findContactByClient_Id(ID)).thenReturn(Optional.of(contact));
        when(repository.findContactByEmail(EMAIL)).thenReturn(Optional.empty());
        when(repository.save(contact)).thenReturn(contact);

        service.changeEmail(ID, EMAIL);

        verify(repository, times(1)).save(contact);
    }

    @Test
    void changeEmail_whenUserNotFound_thenThrowingException() {
        when(repository.findContactByClient_Id(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.changeEmail(ID, EMAIL));

        verify(repository, never()).save(contact);
    }

    @Test
    void changeEmail_whenEmailIsTakenAlready_thenThrowingException() {
        when(repository.findContactByClient_Id(ID)).thenReturn(Optional.of(contact));
        when(repository.findContactByEmail(EMAIL)).thenReturn(Optional.of(contact));

        assertThrows(DuplicateException.class, () -> service.changeEmail(ID, EMAIL));

        verify(repository, never()).save(contact);
    }

    @Test
    void changePushNotificationsTest_whenAllParamsOk_thenSavingInRepository() {
        contact.setPushNotificationEnable(false);
        Contact expectedContact = new Contact();
        expectedContact.setPushNotificationEnable(true);
        when(repository.save(contact)).thenReturn(expectedContact);

        service.changePushNotifications(contact, true);

        verify(repository, times(1)).save(contact);
        Assert.assertEquals(true, expectedContact.getPushNotificationEnable());
    }

    @Test
    void findPassportAndEmail_whenEmailExist_thenReturnTrue() {
        when(repository.findContactByEmail(EMAIL)).thenReturn(Optional.of(contact));

        assertTrue(service.isExistPassportNumberAndEmail(new EmailAndPassportDto(EMAIL, PASSPORT)));

        verify(repository, times(1)).findContactByEmail(EMAIL);
    }

    @Test
    void findPassportAndEmail_whenPassportExist_thenReturnTrue() {
        when(passportDataRepository.findPassportDataByIdentificationPassportNumber(PASSPORT))
                .thenReturn(Optional.of(passportData));

        assertTrue(service.isExistPassportNumberAndEmail(new EmailAndPassportDto(EMAIL, PASSPORT)));

        verify(passportDataRepository, times(1))
                .findPassportDataByIdentificationPassportNumber(PASSPORT);
    }

    @Test
    void findPassportAndEmail_whenPassportAndEmailNotExist_thenReturnFalse() {
        when(passportDataRepository.findPassportDataByIdentificationPassportNumber(PASSPORT))
                .thenReturn(Optional.empty());
        when(repository.findContactByEmail(EMAIL))
                .thenReturn(Optional.empty());

        assertFalse(service.isExistPassportNumberAndEmail(new EmailAndPassportDto(EMAIL, PASSPORT)));

        verify(repository, times(1)).findContactByEmail(EMAIL);
        verify(passportDataRepository, times(1))
                .findPassportDataByIdentificationPassportNumber(PASSPORT);
    }

    @Test
    void getNotificationsInfoByClientId_whenClientNotExist_thenThrowException() {
        UUID nonExistId = UUID.randomUUID();
        when(repository.findContactByClient_Id(nonExistId))
                .thenReturn(Optional.empty());

        assertThrows(UnprocessableEntityException.class, () ->
                service.getNotificationsInfoByClientId(nonExistId));
    }

    @Test
    void getNotificationsInfoByClientId_whenClientExist_thenReturnNotificationsInfoDto() {
        UUID existId = UUID.randomUUID();
        NotificationsInfoDto expectedDto = new NotificationsInfoDto(
                EMAIL, true, true, false);
        Contact existedContact = new Contact(existId, new Client(), true, true,
                false, EMAIL, "71233499512");

        when(repository.findContactByClient_Id(existId))
                .thenReturn(Optional.of(existedContact));

        NotificationsInfoDto actualDto = service.getNotificationsInfoByClientId(existId);

        Assert.assertEquals(expectedDto, actualDto);
    }

    @Test
    void changePushNotificationsTest_whenClientIdNotExist_thenThrowException() {
        UUID nonExistClientId = UUID.randomUUID();
        when(repository.findContactByClient_Id(nonExistClientId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () ->
                service.changeSmsNotifications(nonExistClientId, false));
    }

    @Test
    void changePushNotificationsTest_whenClientIdExist_thenSavingInRepository() {
        UUID existClientId = UUID.randomUUID();
        Contact contact = new Contact(UUID.randomUUID(), new Client(), true,
                true, false, null, "72235467312");

        when(repository.findContactByClient_Id(existClientId)).thenReturn(Optional.of(contact));

        service.changeSmsNotifications(existClientId, false);
        contact.setSmsNotificationEnable(false);
        verify(repository).save(contact);
    }
}