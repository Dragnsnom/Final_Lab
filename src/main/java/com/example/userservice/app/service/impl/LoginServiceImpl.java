package com.example.userservice.app.service.impl;

import com.example.userservice.app.service.LoginService;
import com.example.userservice.app.service.UserProfileService;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.model.PassportData;
import com.example.userservice.persistence.repository.ContactRepository;
import com.example.userservice.persistence.repository.PassportDataRepository;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.requests.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserProfileService userProfileService;
    private final ContactRepository contactRepository;
    private final PassportDataRepository passportDataRepository;

    @Override
    public UUID login(LoginRequestDto loginDataDto) {
        if ("PHONE_NUMBER".equalsIgnoreCase(loginDataDto.getType())) {
            return loginByPhoneNumber(loginDataDto);
        } else if ("PASSPORT_NUMBER".equalsIgnoreCase(loginDataDto.getType())) {
            return loginByPassportNumber(loginDataDto);
        } else throw new BadRequestException("Invalid type");
    }

    private UUID loginByPassportNumber(LoginRequestDto loginDataDto) {
        PassportData passportData = passportDataRepository
                .findPassportDataByIdentificationPassportNumber(loginDataDto.getLogin()).orElseThrow(
                        () -> new UnprocessableEntityException("The record was not found")
                );

        if (userProfileService
                .passwordExists(passportData.getClient().getId(), loginDataDto.getPassword())) {
            return passportData.getClient().getId();
        } else {
            throw new BadRequestException("Invalid password number");
        }
    }

    private UUID loginByPhoneNumber(LoginRequestDto loginDataDto) {
        Contact contact = contactRepository
                .findContactByMobilePhone(loginDataDto.getLogin()).orElseThrow(
                        () -> new UnprocessableEntityException("The record was not found")
                );

        if (userProfileService
                .passwordExists(contact.getClient().getId(), loginDataDto.getPassword())) {
            return contact.getClient().getId();
        } else {
            throw new BadRequestException("Invalid phone number");
        }
    }
}
