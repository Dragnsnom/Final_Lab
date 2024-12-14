package com.example.userservice.app.kafka.consumer;

import com.example.userservice.app.service.completeregistration.CompleteClientRegistrationService;
import com.example.userservice.app.kafka.dto.ApprovedRegisterUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@EnableKafka
@Component
@Slf4j
public class ApprovedRegisterUserConsumer {

    private final Set<CompleteClientRegistrationService> registrationServiceSet;

    /**
     * Метод читает сообщение с топика approved-register-user
     * если Approval = ALLOWED - статус клиента изменяется
     * если Approval = BLOCKED - отмена регистрации
     *
     * @param approvedRegisterUserDto сообщение читаемое с топика
     */
    @KafkaListener(topics = "approved-register-user")
    public void completeClientRegistration(ApprovedRegisterUserDto approvedRegisterUserDto) {
        log.debug("accept message - {}  from topic approved-register-user", approvedRegisterUserDto);
        registrationServiceSet.stream()
                .filter(s -> s.getType() == approvedRegisterUserDto.getClientFlow())
                .findFirst()
                .ifPresent(s -> s.completeClientRegistration(approvedRegisterUserDto));
        log.debug("message processed - {}", approvedRegisterUserDto);
    }
}
