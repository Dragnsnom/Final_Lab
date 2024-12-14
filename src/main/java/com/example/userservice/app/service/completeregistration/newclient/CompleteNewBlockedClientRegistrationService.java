package com.example.userservice.app.service.completeregistration.newclient;

import com.example.userservice.app.kafka.dto.enums.Approval;
import com.example.userservice.app.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompleteNewBlockedClientRegistrationService implements CompleteNewApprovedClientRegistrationService{

    private final ClientService clientService;

    @Override
    public void completeNewApprovedClientRegistration(UUID clientId) {
        log.debug("complete new blocked client registration with id - {}", clientId);
        clientService.deleteById(clientId);
    }

    @Override
    public Approval getType() {
        return Approval.BLOCKED;
    }
}