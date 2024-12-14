package com.example.userservice.app.service;

import com.example.userservice.persistence.model.Contact;
import com.example.userservice.web.dto.requests.EmailAndPassportDto;
import com.example.userservice.web.dto.responses.NotificationsInfoDto;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface ContactService {

    Contact findById(UUID id) throws SQLException;

    List<Contact> findAll();

    void deleteById(UUID id);

    Contact save(Contact contact);

    Boolean isExistPassportNumberAndEmail(EmailAndPassportDto dto);

    void changeEmail(UUID clientId, String email);

    void changePushNotifications(Contact contact, Boolean notificationStatus);

    void changeSmsNotifications(UUID clientId, Boolean notificationStatus);

    Contact findContactByClientId(UUID clientId);

    NotificationsInfoDto getNotificationsInfoByClientId(UUID clientId);
}
