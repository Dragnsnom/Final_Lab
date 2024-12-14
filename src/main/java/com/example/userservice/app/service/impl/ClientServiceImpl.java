package com.example.userservice.app.service.impl;

import com.example.userservice.app.enums.ClientStatus;
import com.example.userservice.app.feign.CreditServiceClient;
import com.example.userservice.app.feign.DepositServiceClient;
import com.example.userservice.app.kafka.dto.RegisterUserDto;
import com.example.userservice.app.kafka.dto.enums.ClientFlow;
import com.example.userservice.app.service.ClientService;
import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.repository.ClientRepository;
import com.example.userservice.persistence.repository.ContactRepository;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.NotFoundException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.requests.ClientDto;
import com.example.userservice.web.dto.requests.NonClientDto;
import com.example.userservice.web.dto.responses.RegistrationInfoDTO;
import com.example.userservice.web.dto.responses.UserInfoDto;
import com.example.userservice.web.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * {@code ClientServiceImpl} class provides implementation for ClientService interface
 * to handle Client-related operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final ContactRepository contactRepository;

    private final ClientMapper clientMapper;

    private final CreditServiceClient creditServiceClient;

    private final DepositServiceClient depositServiceClient;

    private final KafkaTemplate<String, RegisterUserDto> registerUserProducer;

    @Override
    public Client findById(UUID id) throws SQLException {
        return clientRepository.findById(id).orElseThrow(SQLException::new);
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        clientRepository.deleteById(id);
    }

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public RegistrationInfoDTO findByMobilePhone(String mobilePhone) {
        return contactRepository.findContactByMobilePhone(mobilePhone)
                .map(this::mapContactToClient)
                .orElse(new RegistrationInfoDTO(mobilePhone, ClientStatus.NOT_REGISTERED.name()));
    }

    @Override
    @Transactional
    public void nonClientRegistration(NonClientDto nonClientDto) {
        Client client = clientMapper.toClient(nonClientDto);
        client.setClientStatus(ClientStatus.IN_PROCESSING);
        clientRepository.save(client);
        registerUserProducer.send("register-user",
                new RegisterUserDto(client.getId(), client.getContact().getEmail(), ClientFlow.NEW));
    }

    @Override
    @Transactional
    public void clientRegistration(ClientDto clientDto) {
        Client client = clientRepository.findById(clientDto.getId())
                .orElseThrow(() ->
                        new UnprocessableEntityException("non-existent client - " + clientDto.getId()));

        client.setContact(clientMapper.toContact(clientDto));
        client.setUserProfile(clientMapper.toUserProfile(clientDto));
        client.setClientStatus(ClientStatus.IN_PROCESSING);
        clientRepository.save(client);
    }

    /**
     * Return the {@code UserInfoDto} representing information about the user from database
     *
     * @param clientId the {@code UUID} identification number of the bank's client
     * @return {@code UserInfoDto} result with information about the bank's client
     */
    @Override
    public UserInfoDto getUserInfoById(UUID clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(
                () -> new NotFoundException("Client Not Found: " + clientId));

        return UserInfoDto.builder()
                .firstName(client.getFirstName())
                .surname(client.getSurname())
                .lastName(client.getLastName())
                .passportNumber(client.getPassportData().getIdentificationPassportNumber())
                .email(client.getContact().getEmail())
                .mobilePhone(client.getContact().getMobilePhone())
                .build();
    }

    /**
     * Изменение статуса клиента по id
     *
     * @param clientId     уникальный идентификатор клиента.
     * @param clientStatus новый статус клиента.
     */
    @Override
    @Transactional
    public void changeClientStatus(UUID clientId, ClientStatus clientStatus) {
        log.debug("changeClientStatus with id - {}, to {}", clientId, clientStatus);
        clientRepository.changeClientStatusById(clientId, clientStatus);
        log.debug("status have been changed");
    }

    /**
     * Отмена регистрации клиента,
     * удаление его записей из таблиц Contact и userProfile
     *
     * @param clientId     уникальный идентификатор клиента.
     */
    @Override
    @Transactional
    public void cancelClientRegistration(UUID clientId) {
        log.debug("cancelClientRegistration with id - {}, remove contact and userProfile", clientId);
        clientRepository.findById(clientId).ifPresentOrElse(client -> {
            contactRepository.deleteById(client.getContact().getId());
            client.setContact(null);
            client.setUserProfile(null);
            clientRepository.changeClientStatusById(clientId, ClientStatus.NOT_CLIENT);
            log.debug("contact and userProfile have been deleted, status changed");
        }, () -> log.error("client with id - {} is not exist", clientId));
    }

    /**
     * Проверка наличия активных продуктов клиента по id
     * в deposit-service и credit-service
     *
     * @param clientId     уникальный идентификатор клиента.
     */
    @Override
    public boolean hasActiveProducts(UUID clientId) {
        return (hasClientActiveCredits(clientId) || hasClientActiveDeposits(clientId));
    }

    private RegistrationInfoDTO mapContactToClient(Contact contact) {
        return clientRepository.findClientByContact(contact)
                .map(client -> creationDTO(contact, client))
                .orElseThrow(() -> new BadRequestException("The client does not exist"));
    }

    private RegistrationInfoDTO creationDTO(Contact contact, Client client) {
        RegistrationInfoDTO infoDto = new RegistrationInfoDTO(
                contact.getMobilePhone(),
                client.getClientStatus().name()
        );

        if (client.getClientStatus().equals(ClientStatus.NOT_CLIENT)) {
            infoDto.setClientId(client.getId().toString());
        }

        return infoDto;
    }

    private boolean hasClientActiveCredits(UUID clientId) {
        ResponseEntity checkClientCreditsResponse = creditServiceClient.checkClientCredits(clientId);
        return checkClientCreditsResponse.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200));
    }

    private boolean hasClientActiveDeposits(UUID clientId) {
        ResponseEntity checkClientDepositsResponse = depositServiceClient.checkClientDeposits(clientId);
        return checkClientDepositsResponse.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200));
    }
}
