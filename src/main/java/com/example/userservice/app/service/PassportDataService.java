package com.example.userservice.app.service;

import com.example.userservice.persistence.model.PassportData;
import com.example.userservice.web.dto.responses.MobilePhoneDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface PassportDataService {

    PassportData findById(UUID id) throws SQLException;

    List<PassportData> findAll();

    void deleteById(UUID id);

    PassportData save(PassportData passportData);

    MobilePhoneDTO getPhoneNumberByPassportNumber(String passportNumber);
}
