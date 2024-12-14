package com.example.userservice.app.service.completeregistration.oldclient;

import com.example.userservice.app.kafka.dto.enums.Approval;
import com.example.userservice.app.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompleteOldBlockedClientRegistrationService implements CompleteOldApprovedClientRegistrationService{

    private final ClientService clientService;
    @Override
    public void completeOldApprovedClientRegistration(UUID clientId) {
        log.debug("complete old blocked client registration with id - {}", clientId);
        clientService.cancelClientRegistration(clientId);
    }

    @Override
    public Approval getType() {
        return Approval.BLOCKED;
    }
}
