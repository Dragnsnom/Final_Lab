package com.example.userservice.app.service;

import com.example.userservice.app.service.impl.VerificationServiceImpl;
import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.model.Verification;
import com.example.userservice.persistence.repository.ContactRepository;
import com.example.userservice.persistence.repository.VerificationRepository;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.ForbiddenException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.controller.exception.ViolationBlockingPeriodException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class VerificationServiceTest {

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private VerificationServiceImpl verificationService;

    @Test
    void generateCodeByMobilePhoneWithoutBlockExpiration() {
        String mobilePhone = "79345612385";
        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.randomUUID());
        contact.setMobilePhone(mobilePhone);
        contact.setClient(client);
        Optional<Contact> contactOptional = Optional.of(contact);

        Mockito.when(contactRepository.findContactByMobilePhone(mobilePhone))
                .thenReturn(contactOptional);

        Verification verification = new Verification();
        verification.setBlockExpiration(null);
        Optional<Verification> verificationOptional = Optional.of(verification);

        Mockito.when(verificationRepository.findById(contact.getMobilePhone()))
                .thenReturn(verificationOptional);

        Mockito.when(verificationRepository.save(any(Verification.class)))
                .thenReturn(verification);

        verificationService.generateCodeByMobilePhone(mobilePhone);
        Mockito.verify(verificationRepository).save(any(Verification.class));
    }

    @Test
    void generateCodeByMobilePhoneWithBlockExpirationAfterNow() {
        String mobilePhone = "79345612385";
        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.randomUUID());
        contact.setMobilePhone(mobilePhone);
        contact.setClient(client);
        Optional<Contact> contactOptional = Optional.of(contact);

        Mockito.when(contactRepository.findContactByMobilePhone(mobilePhone))
                .thenReturn(contactOptional);

        Verification verification = new Verification();
        verification.setBlockExpiration(LocalDateTime.MAX);
        Optional<Verification> verificationOptional = Optional.of(verification);

        Mockito.when(verificationRepository.findById(contact.getMobilePhone()))
                .thenReturn(verificationOptional);

        assertThrows(ViolationBlockingPeriodException.class, () ->
                verificationService.generateCodeByMobilePhone(mobilePhone));
    }

    @Test
    void generateCodeByMobilePhoneWithoutExistingVerification() {
        String mobilePhone = "79345612385";
        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.randomUUID());
        contact.setMobilePhone(mobilePhone);
        contact.setClient(client);
        Optional<Contact> contactOptional = Optional.of(contact);

        Mockito.when(contactRepository.findContactByMobilePhone(mobilePhone))
                .thenReturn(contactOptional);

        Optional<Verification> verificationOptional = Optional.ofNullable(new Verification());

        Mockito.when(verificationRepository.findById(contact.getMobilePhone()))
                .thenReturn(verificationOptional);

        verificationService.generateCodeByMobilePhone(mobilePhone);
        Mockito.verify(verificationRepository).save(any(Verification.class));
    }

    @Test
    void generateCodeByPassportNumberWithoutBlockExpiration() {
        String passportNumber = "AB3427796";
        String mobilePhone = "79345612385";
        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.randomUUID());
        contact.setMobilePhone(mobilePhone);
        contact.setClient(client);
        Optional<Contact> contactOptional = Optional.of(contact);

        Mockito.when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(passportNumber))
                .thenReturn(contactOptional);

        Verification verification = new Verification();
        verification.setBlockExpiration(null);
        Optional<Verification> verificationOptional = Optional.of(verification);

        Mockito.when(verificationRepository.findById(contact.getMobilePhone()))
                .thenReturn(verificationOptional);

        Mockito.when(verificationRepository.save(any(Verification.class)))
                .thenReturn(verification);

        verificationService.generateCodeByPassportNumber(passportNumber);
        Mockito.verify(verificationRepository).save(any(Verification.class));
    }

    @Test
    void generateCodeByPassportNumberWithBlockExpirationAfterNow() {
        String passportNumber = "AB3427796";
        String mobilePhone = "79345612385";
        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.randomUUID());
        contact.setMobilePhone(mobilePhone);
        contact.setClient(client);
        Optional<Contact> contactOptional = Optional.of(contact);

        Mockito.when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(passportNumber))
                .thenReturn(contactOptional);

        Verification verification = new Verification();
        verification.setBlockExpiration(LocalDateTime.MAX);
        Optional<Verification> verificationOptional = Optional.of(verification);

        Mockito.when(verificationRepository.findById(contact.getMobilePhone()))
                .thenReturn(verificationOptional);

        assertThrows(ViolationBlockingPeriodException.class, () ->
                verificationService.generateCodeByPassportNumber(passportNumber));
    }

    @Test
    void generateCodeByPassportNumberWithBlockExpirationBeforeNow() {
        String passportNumber = "AB3427796";
        String mobilePhone = "79345612385";
        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.randomUUID());
        contact.setMobilePhone(mobilePhone);
        contact.setClient(client);
        Optional<Contact> contactOptional = Optional.of(contact);

        Mockito.when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(passportNumber))
                .thenReturn(contactOptional);

        Verification verification = new Verification();
        verification.setBlockExpiration(LocalDateTime.MIN);
        Optional<Verification> verificationOptional = Optional.of(verification);

        Mockito.when(verificationRepository.findById(contact.getMobilePhone()))
                .thenReturn(verificationOptional);

        Mockito.when(verificationRepository.save(any(Verification.class)))
                .thenReturn(verification);

        verificationService.generateCodeByPassportNumber(passportNumber);
        Mockito.verify(verificationRepository).save(any(Verification.class));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeTest() {
        String mobilePhone = "79345612385";

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                0,
                LocalDateTime.now()
        );

        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(BadRequestException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCode("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithThreeAttemptsTest() {
        String mobilePhone = "79345612385";

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                2,
                LocalDateTime.now());

        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(ForbiddenException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCode("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithTwoAttemptsTest() {
        String mobilePhone = "79345612385";

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                1,
                LocalDateTime.now());

        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(BadRequestException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCode("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithNonExistentVerificationCodeTest() {
        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCode("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithNotExpiredBlockingPeriodTest() {
        String mobilePhone = "79345612385";

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                LocalDateTime.now().plusMinutes(5),
                2,
                LocalDateTime.now());

        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(ForbiddenException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCode("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithNotExpiredCode() {
        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCode("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithNonExistentPhoneNumberTest() {
        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCode("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithAuthTest() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                0,
                LocalDateTime.now()
        );

        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString())).thenReturn(Optional.of(contact));
        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(BadRequestException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCodeWithAuth("", ""));

    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithAuthAndThreeAttemptsTest() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                2,
                LocalDateTime.now()
        );

        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString())).thenReturn(Optional.of(contact));
        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(ForbiddenException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithAuthAndTwoAttemptsTest() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                1,
                LocalDateTime.now()
        );

        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString())).thenReturn(Optional.of(contact));
        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(BadRequestException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithAuthAndNonExistentVerificationCodeTest() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);


        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString())).thenReturn(Optional.of(contact));
        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithAuthAndNotExpiredBlockingPeriodTest() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                2,
                LocalDateTime.now()
        );

        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString())).thenReturn(Optional.of(contact));
        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(ForbiddenException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithAuthAndNotExpiredCode() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString())).thenReturn(Optional.of(contact));
        Mockito.when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByMobilePhoneAndVerificationCodeWithAuthAndNonExistentPhoneNumberTest() {
        Mockito.when(contactRepository.findContactByMobilePhone(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                verificationService.verifyByMobilePhoneAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByPassportNumberAndVerificationCodeWithAuth_Test() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                0,
                LocalDateTime.now()
        );

        when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(Mockito.any())).thenReturn(Optional.of(contact));
        when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(BadRequestException.class, () ->
                verificationService.verifyByPassportNumberAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByPassportNumberAndVerificationCodeWithAuth_ThreeAttemptsTest() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                2,
                LocalDateTime.now()
        );

        when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(Mockito.any())).thenReturn(Optional.of(contact));
        when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(ForbiddenException.class, () ->
                verificationService.verifyByPassportNumberAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByPassportNumberAndVerificationCodeWithAuth_TwoAttemptsTest() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                1,
                LocalDateTime.now()
        );

        when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(Mockito.any())).thenReturn(Optional.of(contact));
        when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(BadRequestException.class, () ->
                verificationService.verifyByPassportNumberAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByPassportNumberAndVerificationCodeWithAuth_NonExistentVerificationCodeTest() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(Mockito.anyString())).thenReturn(Optional.of(contact));
        when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                verificationService.verifyByPassportNumberAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByPassportNumberAndVerificationCodeWithAuth_NotExpiredBlockingPeriodTest() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        Verification verification = new Verification(
                mobilePhone,
                "140978",
                null,
                2,
                LocalDateTime.now()
        );

        when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(Mockito.anyString())).thenReturn(Optional.of(contact));
        when(verificationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(verification));

        Assertions.assertThrows(ForbiddenException.class, () ->
                verificationService.verifyByPassportNumberAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByPassportNumberAndVerificationCodeWithAuth_NotExpiredCode() {
        String mobilePhone = "79345612385";

        Contact contact = new Contact();
        Client client = new Client();
        client.setId(UUID.fromString("178331c2-9a6e-11ee-b9d1-0242ac120002"));
        contact.setClient(client);
        contact.setMobilePhone(mobilePhone);

        when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(Mockito.anyString())).thenReturn(Optional.of(contact));
        when(verificationRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                verificationService.verifyByPassportNumberAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByPassportNumberAndVerificationCodeWithAuth_NonExistentPhoneNumberTest() {
        Contact contact = new Contact();

        when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(Mockito.anyString())).thenReturn(Optional.of(contact));

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                verificationService.verifyByPassportNumberAndVerificationCodeWithAuth("", ""));
    }

    @Test
    void verifyByPassportNumberAndVerificationCodeWithAuth_NonExistentPassportNumber() {

        when(contactRepository.findByClient_PassportData_IdentificationPassportNumber(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnprocessableEntityException.class, () ->
                verificationService.verifyByPassportNumberAndVerificationCodeWithAuth("", ""));
    }
}