package com.example.userservice.app.service.impl;

import com.example.userservice.app.service.PassportDataService;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.model.PassportData;
import com.example.userservice.persistence.repository.ContactRepository;
import com.example.userservice.persistence.repository.PassportDataRepository;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.responses.MobilePhoneDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassportDataServiceImpl implements PassportDataService {

    private final PassportDataRepository passportDataRepository;

    private final ContactRepository contactRepository;

    @Override
    public PassportData findById(UUID id) throws SQLException {
        return passportDataRepository.findById(id).orElseThrow(SQLException::new);
    }

    @Override
    public List<PassportData> findAll() {
        return passportDataRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        passportDataRepository.deleteById(id);
    }

    @Override
    public PassportData save(PassportData passportData) {
        return passportDataRepository.save(passportData);
    }

    @Override
    public MobilePhoneDTO getPhoneNumberByPassportNumber(String passportNumber) {
        return contactRepository
                .findByClient_PassportData_IdentificationPassportNumber(passportNumber)
                .map(Contact::getMobilePhone)
                .map(MobilePhoneDTO::new)
                .orElseThrow(() -> new UnprocessableEntityException("phone number not found"));
    }


}
