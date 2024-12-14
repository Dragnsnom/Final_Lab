package com.example.userservice.app.service.impl;

import com.example.userservice.app.enums.AuthorizationType;
import com.example.userservice.app.service.UserProfileService;
import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.UserProfile;
import com.example.userservice.persistence.repository.ClientRepository;
import com.example.userservice.persistence.repository.UserProfileRepository;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.InternalServerException;
import com.example.userservice.web.controller.exception.NotFoundException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.requests.AuthorizationTypeIncomingDto;
import com.example.userservice.web.dto.responses.AuthResponseDto;
import com.example.userservice.web.dto.responses.AuthorizationOutgoingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private final UserProfileRepository userProfileRepository;

    private final ClientRepository clientRepository;

    private static final String BIOMETRICS = "biometrics";
    private static final String PINCODE = "pincode";
    private static final String LOGPASS = "logpass";

    @Override
    public UserProfile findById(UUID id) throws SQLException {
        return userProfileRepository.findById(id).orElseThrow(SQLException::new);
    }

    @Override
    public UserProfile findUserProfileByClientId(UUID clientId) {
        if (clientId == null) {
            throw new NullPointerException("Client ID cannot be null");
        }
        return userProfileRepository.findUserProfileByClientId(clientId);
    }

    @Override
    public List<UserProfile> findAll() {
        return userProfileRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        userProfileRepository.deleteById(id);
    }

    @Override
    public UserProfile save(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    public HttpStatus resetPassword(UUID id, String password) {
        return clientRepository.findById(id).map(client -> mapClientToProfile(client, password))
                .orElseThrow(() -> new UnprocessableEntityException("The client does not exist"));
    }

    private HttpStatus mapClientToProfile(Client client, String password) {
        return userProfileRepository.findUserProfileByClient(client)
                .map(userProfile -> encodeAndSavePassword(userProfile, password))
                .orElseThrow(() -> new UnprocessableEntityException("The user profile does not exist"));
    }

    private HttpStatus encodeAndSavePassword(UserProfile userProfile, String password) {
        userProfile.setPasswordEncoded(ENCODER.encode(password));
        try {
            userProfileRepository.save(userProfile);
            return HttpStatus.OK;
        } catch (Exception e) {
            throw new InternalServerException("Password change error");
        }
    }

    /**
     * Updates the password for a user with the given ID.
     * Retrieves the user profile by the provided ID, logs the password update action,
     * and encodes the new password before saving it to the user profile.
     *
     * @param id          The UUID identifying the user whose password needs to be updated.
     * @param newPassword The new password to be set for the user.
     * @Transactional ensures that the update operation is executed within a transaction.
     */
    @Transactional
    @Override
    public void updatePassword(UUID id, String newPassword) {
        UserProfile userProfile = userProfileRepository.findUserProfileByClientId(id);

        encodeAndSavePassword(userProfile, newPassword);
    }

    /**
     * Checks if the provided old password matches the stored password for the user identified by the given ID.
     * Retrieves the user profile by the provided ID and compares the stored password with the provided old password.
     *
     * @param id          The UUID identifying the user to check the password for.
     * @param oldPassword The old password to verify against the stored password.
     * @return true if the provided old password matches the stored password, false otherwise.
     */
    @Override
    public boolean passwordExists(UUID id, String oldPassword) {
        UserProfile userProfile = userProfileRepository.findUserProfileByClientId(id);

        String oldPasswordInDB = userProfile.getPasswordEncoded();
        return ENCODER.matches(oldPassword, oldPasswordInDB);
    }

    /**
     * Changing the type of authorization.
     * Returns chosen by user authorization type.
     *
     * @param profile   profile needs to change authorization type.
     * @param dto       DTO with three fields, only one is true.
     * @return dto with String field with new type of authorization.
     */
    public AuthorizationOutgoingDto changeAuthorizationType(UserProfile profile,
                                                            AuthorizationTypeIncomingDto dto) {
        String authorizationTypeString = extractAuthorizationType(dto);
        AuthorizationType authorizationType = AuthorizationType.valueOf(authorizationTypeString.toUpperCase());

        if (profile.getAuthorization() == authorizationType) {
            return new AuthorizationOutgoingDto(authorizationTypeString);
        }

        profile.setAuthorization(authorizationType);
        userProfileRepository.save(profile);
        return new AuthorizationOutgoingDto(authorizationTypeString);
    }

    private String extractAuthorizationType (AuthorizationTypeIncomingDto dto) {
        if (dto.isBiometrics()) {
            return BIOMETRICS;
        } else if (dto.isPincode()) {
            return PINCODE;
        } else {
            return LOGPASS;
        }
    }

    /**
     * Updates the security question and answer for a user profile identified by the provided UUID.
     *
     * @param id               The unique identifier (UUID) of the user profile.
     * @param securityQuestion The new security question to set for the user profile.
     * @param securityAnswer   The new security answer corresponding to the security question.
     * @throws InternalServerException If an error occurs while updating the security question and answer.
     *                                This exception is thrown when the operation fails to save the changes.
     */
    @Transactional
    @Override
    public void updateSecurityQuestion(UUID id, String securityQuestion, String securityAnswer) {
        UserProfile userProfile = userProfileRepository.findUserProfileByClientId(id);
        userProfile.setSecurityQuestion(securityQuestion);
        userProfile.setSecurityAnswer(securityAnswer);

        try {
            userProfileRepository.save(userProfile);
        } catch (Exception e) {
            throw new InternalServerException("Security question change failed for client with ID: " + id);
        }
    }

    @Override
    public HttpStatus saveFingerprint(UUID clientId, String fingerprint) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isEmpty()) {
            throw new NotFoundException("Client not found for client with ID:" + clientId);
        }
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByClientId(clientId);
        userProfileOptional.ifPresent(userProfile -> {
            userProfile.setFingerprint(fingerprint);
            userProfileRepository.save(userProfile);
        });
        userProfileOptional.orElseThrow(() -> new NotFoundException("User profile not found for client with ID " + clientId));
        return HttpStatus.OK;
    }

    @Override
    public AuthResponseDto authorizationByFingerprint(String clientId, String fingerprint) {
        Optional<Client> clientOptional = clientRepository.findById(UUID.fromString(clientId));
        return clientOptional.flatMap(userProfileRepository::findUserProfileByClient)
                .filter(userProfile -> userProfile.getFingerprint().equals(fingerprint))
                .map(userProfile -> new AuthResponseDto(userProfile.getClient().getId()))
                .orElseThrow(() -> new BadRequestException("Incorrect data entered"));
    }
}
