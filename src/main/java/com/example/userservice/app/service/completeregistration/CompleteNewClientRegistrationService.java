package com.example.userservice.app.service.completeregistration;

import com.example.userservice.app.service.completeregistration.CompleteClientRegistrationService;
import com.example.userservice.app.service.completeregistration.newclient.CompleteNewApprovedClientRegistrationService;
import com.example.userservice.app.kafka.dto.ApprovedRegisterUserDto;
import com.example.userservice.app.kafka.dto.enums.ClientFlow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompleteNewClientRegistrationService implements CompleteClientRegistrationService {

    private final Set<CompleteNewApprovedClientRegistrationService> registrationServiceSet;

    @Override
    public void completeClientRegistration(ApprovedRegisterUserDto approvedRegisterUserDto) {
        log.debug("choosing serviceRegistration type for new client with id - {}", approvedRegisterUserDto);
        registrationServiceSet.stream()
                .filter(s -> s.getType() == approvedRegisterUserDto.getApproval())
                .findFirst()
                .ifPresent(s -> s.completeNewApprovedClientRegistration(approvedRegisterUserDto.getClientId()));
    }

    @Override
    public ClientFlow getType() {
        return ClientFlow.NEW;
    }
}
