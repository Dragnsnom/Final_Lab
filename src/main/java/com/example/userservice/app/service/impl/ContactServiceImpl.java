package com.example.userservice.app.service.impl;

import com.example.userservice.app.service.ContactService;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.repository.ContactRepository;
import com.example.userservice.persistence.repository.PassportDataRepository;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.DuplicateException;
import com.example.userservice.web.controller.exception.NotFoundException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.requests.EmailAndPassportDto;
import com.example.userservice.web.dto.responses.NotificationsInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.userservice.web.util.Constant.DUPLICATE_EMAIL_MSG;
import static com.example.userservice.web.util.Constant.USER_NOT_FOUND_MSG;

/**
 * ContactServiceImpl class provides implementation for ContactService interface
 * to handle Contact-User-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final PassportDataRepository passportDataRepository;

    @Override
    public Contact findById(UUID id) throws SQLException {
        return contactRepository.findById(id).orElseThrow(SQLException::new);
    }

    @Override
    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        contactRepository.deleteById(id);
    }

    @Override
    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }

    /**
     * Return the {@code Boolean} checks the availability of the email and passport number in database
     *
     * @param dto the {@code EmailAndPassportDto} object with data for passing the availability check
     * @return {@code Boolean} the result of the availability check in database
     */
    @Override
    public Boolean isExistPassportNumberAndEmail(EmailAndPassportDto dto) {
        return contactRepository.findContactByEmail(dto.getEmail()).isPresent() ||
                passportDataRepository.findPassportDataByIdentificationPassportNumber(dto.getPassportNumber())
                        .isPresent();
    }

    /**
     * Method for changing email of client in {@code ContactRepository}
     * If client not found or email been taken already then throwing exception.
     *
     * @param clientId - id of client, who change email
     * @param email    - a new e-mail
     */
    @Transactional
    public void changeEmail(UUID clientId, String email) {
        Contact contactFoundByClientId = findContactByClientId(clientId);
        Optional<Contact> contactFoundByEmail = contactRepository.findContactByEmail(email);
        if (contactFoundByEmail.isPresent()) {
            throw new DuplicateException(DUPLICATE_EMAIL_MSG + email);
        }
        contactFoundByClientId.setEmail(email);
        save(contactFoundByClientId);
        log.info("E-mail for contact = {} changed", contactFoundByClientId);
    }

    /**
     * Method for changing push notification status of client in {@code ContactRepository}
     * If notification status already equals getting to method status, do nothing
     *
     * @param contact            - contact of client, who change push notification status
     * @param notificationStatus - true / false boolean to change push notification status
     */
    public void changePushNotifications(Contact contact, Boolean notificationStatus) {
        contact.setPushNotificationEnable(notificationStatus);
        Contact contactFromRepository = contactRepository.save(contact);
        if (contactFromRepository.equals(contact)) {
            log.info("Push notifications for contact = {} changed to {}", contact, notificationStatus);
        }
    }

    /**
     * Searching {@code Contact} by client ID.
     *
     * @param clientId  - id of client
     * @return          - {@code Contact} which was found
     * @throws          - exception if user not found
     */
    @Override
    public Contact findContactByClientId(UUID clientId) {
        return contactRepository.findContactByClient_Id(clientId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MSG + clientId.toString()));
    }

    @Override
    public NotificationsInfoDto getNotificationsInfoByClientId(UUID clientId) {
        Contact contact = contactRepository.findContactByClient_Id(clientId)
                .orElseThrow(() ->
                        new UnprocessableEntityException("non-existent client - " + clientId));

        if (contact.getEmail() == null) {
            return new NotificationsInfoDto("", contact.getSmsNotificationEnable(),
                    contact.getPushNotificationEnable(), contact.getEmailNotificationEnable());
        } else {
            return new NotificationsInfoDto(contact.getEmail(), contact.getSmsNotificationEnable(),
                    contact.getPushNotificationEnable(), contact.getEmailNotificationEnable());
        }
    }

    @Override
    public void changeSmsNotifications(UUID clientId, Boolean notificationStatus) {
        Contact contact = contactRepository.findContactByClient_Id(clientId)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND_MSG + clientId));

        contact.setSmsNotificationEnable(notificationStatus);
        contactRepository.save(contact);
    }
}
