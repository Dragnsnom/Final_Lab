package com.example.userservice.app.service.impl;

import com.example.userservice.app.service.VerificationService;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.model.Verification;
import com.example.userservice.persistence.repository.ContactRepository;
import com.example.userservice.persistence.repository.VerificationRepository;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.ClientNotFoundException;
import com.example.userservice.web.controller.exception.ForbiddenException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.controller.exception.ViolationBlockingPeriodException;
import com.example.userservice.web.dto.responses.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.time.Duration.between;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class VerificationServiceImpl implements VerificationService {

    private final VerificationRepository verificationRepository;

    private final ContactRepository contactRepository;

    public Optional<Verification> findById(String mobilePhone) {
        return verificationRepository.findById(mobilePhone);
    }

    public List<Verification> findAll() {
        return StreamSupport.stream(verificationRepository.findAll().spliterator(), false)
                .toList();
    }

    public void deleteById(String mobilePhone) {
        verificationRepository.deleteById(mobilePhone);
    }

    public Verification save(Verification entity) {
        return verificationRepository.save(entity);
    }

    public void generateCodeByMobilePhone(String mobilePhone) {
        generateCodeById(mobilePhone);
    }

    public void generateCodeByPassportNumber(String passportNumber) {
        Optional<Contact> optional = contactRepository.findByClient_PassportData_IdentificationPassportNumber(passportNumber);

        if (optional.isPresent()) {
            String mobilePhone = optional.get().getMobilePhone();
            if (!mobilePhone.isEmpty()) {
                generateCodeById(mobilePhone);
            } else {
                throw new UnprocessableEntityException("Mobile phone number is missing or empty");
            }
        } else {
            throw new UnprocessableEntityException("Contact not found for the given passport number");
        }
    }

    public AuthResponseDto verifyByMobilePhoneAndVerificationCodeWithAuth(String mobilePhone, String verificationCode) {
        if (contactRepository.findContactByMobilePhone(mobilePhone).isEmpty()) {
            throw new UnprocessableEntityException("The phone number was not found");
        }

        Verification verification = getVerification(mobilePhone);

        return checkClientsBlockAndGenerateAuthResponseDto(verification, verificationCode);
    }

    @Override
    public AuthResponseDto verifyByPassportNumberAndVerificationCodeWithAuth(String passportNumber, String verificationCode) {
        Optional<Contact> optional = contactRepository.findByClient_PassportData_IdentificationPassportNumber(passportNumber);
        String mobilePhone = optional.map(Contact::getMobilePhone).orElseThrow(() -> new UnprocessableEntityException("The passport number was not found"));

        Verification verification = getVerification(mobilePhone);

        return checkClientsBlockAndGenerateAuthResponseDto(verification, verificationCode);
    }

    public void verifyByMobilePhoneAndVerificationCode(String mobilePhone, String verificationCode) {
        Verification verification = getVerification(mobilePhone);

        checkClientsBlock(verification, verificationCode);
    }

    private Verification getVerification(String mobilePhone) {
        return verificationRepository.findById(mobilePhone)
                .orElseThrow(() -> {
                    generateCodeByMobilePhone(mobilePhone);
                    return new UnprocessableEntityException("The verification code does not exist");
                });
    }

    private void checkClientsBlock(Verification verification, String verificationCode) {
        if (verification.getBlockExpiration() != null) {
            checkBlockingPeriodHasExpired(verification);
        } else {
            verification.setVerificationAttempts(verification.getVerificationAttempts() + 1);
            verificationRepository.save(verification);
            if (verificationCode.equals(verification.getVerificationCode())) {
                verificationRepository.deleteById(verification.getMobilePhone());
            } else {
                checkVerificationAttempts(verification);
            }
        }
    }

    private AuthResponseDto checkClientsBlockAndGenerateAuthResponseDto(Verification verification, String verificationCode) {
        if (verification.getBlockExpiration() != null) {
            checkBlockingPeriodHasExpired(verification);
        } else {
            verification.setVerificationAttempts(verification.getVerificationAttempts() + 1);
            verificationRepository.save(verification);
            if (verificationCode.equals(verification.getVerificationCode())) {
                AuthResponseDto authResponseDto = new AuthResponseDto(
                        contactRepository.findContactByMobilePhone(verification.getMobilePhone())
                                .orElseThrow(() -> new ClientNotFoundException(verification.getMobilePhone()))
                                .getClient().getId());
                verificationRepository.deleteById(verification.getMobilePhone());

                return authResponseDto;
            } else {
                checkVerificationAttempts(verification);
            }
        }

        return null;
    }

    private void checkBlockingPeriodHasExpired(Verification verification) {
        if (verification.getBlockExpiration().isAfter(LocalDateTime.now())) {
            throw new ForbiddenException("The blocking period has not expired", verification.getBlockExpiration());
        } else {
            String mobilePhone = verification.getMobilePhone();
            verificationRepository.deleteById(verification.getMobilePhone());
            generateCodeById(mobilePhone);
            throw new UnprocessableEntityException("The blocking period has expired");
        }
    }

    private void checkVerificationAttempts(Verification verification) {
        if (verification.getVerificationAttempts() == 3) {
            verification.setBlockExpiration(LocalDateTime.now().plusMinutes(10));
            verificationRepository.save(verification);
            throw new ForbiddenException("The attempts are over", verification.getBlockExpiration());
        } else {
            throw new BadRequestException("Incorrect verification code");
        }
    }

    private void generateCodeById(String mobilePhone) {
        Optional<Verification> ver = verificationRepository.findById(mobilePhone);
        if (ver.isPresent()) {
            Verification verification = ver.get();
            if (verification.getBlockExpiration() == null ||
                    verification.getBlockExpiration().isBefore(LocalDateTime.now())) {
                verification.setVerificationCode(generateVerificationCode());
                verification.setCodeLifetime(LocalDateTime.now());
                verificationRepository.save(verification);
            } else {
                Duration duration = between(verification.getBlockExpiration(), LocalDateTime.now());
                throw new ViolationBlockingPeriodException(duration.toSeconds() +
                        " seconds - Until the end of the blocking");
            }
        } else {
            Verification newVerification = new Verification(
                    mobilePhone, generateVerificationCode(), null, 0, LocalDateTime.now());
            verificationRepository.save(newVerification);
        }
    }

    private String generateVerificationCode() {
        int verificationCodeLength = 6;
        return RandomStringUtils.randomNumeric(verificationCodeLength);
    }
}


