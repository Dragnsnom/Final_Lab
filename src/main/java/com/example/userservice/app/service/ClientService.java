package com.example.userservice.app.service;

import com.example.userservice.app.enums.ClientStatus;
import com.example.userservice.persistence.model.Client;
import com.example.userservice.web.dto.requests.ClientDto;
import com.example.userservice.web.dto.requests.NonClientDto;
import com.example.userservice.web.dto.responses.RegistrationInfoDTO;
import com.example.userservice.web.dto.responses.UserInfoDto;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface ClientService {

    Client findById(UUID id) throws SQLException;

    List<Client> findAll();

    void deleteById(UUID id);

    Client save(Client client);

    RegistrationInfoDTO findByMobilePhone(String mobilePhone);

    void nonClientRegistration(NonClientDto nonClientDto);

    void clientRegistration(ClientDto clientDto);

    UserInfoDto getUserInfoById(UUID clientId);

    void changeClientStatus(UUID clientId, ClientStatus clientStatus);

    void cancelClientRegistration(UUID clientId);

    boolean hasActiveProducts(UUID clientId);
}
