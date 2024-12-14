package com.example.userservice.app.service;

import com.example.userservice.app.enums.AuthorizationType;
import com.example.userservice.app.service.impl.UserProfileServiceImpl;
import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.UserProfile;
import com.example.userservice.persistence.repository.ClientRepository;
import com.example.userservice.persistence.repository.UserProfileRepository;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.InternalServerException;
import com.example.userservice.web.controller.exception.NotFoundException;
import com.example.userservice.web.controller.exception.NotFoundException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.dto.requests.AuthorizationTypeIncomingDto;
import com.example.userservice.web.dto.responses.AuthResponseDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Test
    void testFindUserProfileByClientId() {
        UUID clientId = UUID.randomUUID();

        Client client = new Client();
        client.setId(clientId);

        UserProfile userProfile = new UserProfile();
        userProfile.setClient(client);
        userProfile.setSecurityAnswer("test");
        userProfile.setSecurityQuestion("test");
        when(userProfileRepository.findUserProfileByClientId(clientId)).thenReturn(userProfile);

        UserProfile result = userProfileService.findUserProfileByClientId(clientId);

        verify(userProfileRepository, times(1)).findUserProfileByClientId(clientId);

        assertEquals(userProfile, result);
    }

    @Test
    void testFindUserProfileByClientIdWithNullClientId() {
        assertThrows(NullPointerException.class, () -> {
            userProfileService.findUserProfileByClientId(null);
        });

        verifyNoInteractions(userProfileRepository);
    }

    @Test
    void resetPasswordTest() {

        when(clientRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Client()));
        when(userProfileRepository.findUserProfileByClient(any(Client.class))).thenReturn(Optional.of(new UserProfile()));
        when(userProfileRepository.save(any())).thenReturn(new UserProfile());

        assertEquals(HttpStatus.OK, userProfileService.resetPassword(UUID.randomUUID(),
                "Pa$$w0rd"));

    }

    @Test
    void resetPasswordTestWithNonExistentClient() {

        when(clientRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(UnprocessableEntityException.class, () ->
                userProfileService.resetPassword(UUID.randomUUID(), "Pa$$w0rd"));

    }

    @Test
    void resetPasswordTestWithNonExistentProfile() {

        when(clientRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Client()));
        when(userProfileRepository.findUserProfileByClient(any(Client.class))).thenReturn(Optional.empty());

        assertThrows(UnprocessableEntityException.class, () ->
                userProfileService.resetPassword(UUID.randomUUID(), "Pa$$w0rd"));

    }

    @Test
    void updatePassword_Success() {
        UUID clientId = UUID.randomUUID();
        String oldPassword = "Pa$$w0rd";
        String newPassword = "Pa$$word123";

        when(encoder.encode(oldPassword)).thenReturn("$2a$10$teBfwK/XgvfczEmnqwAKz.FwEXqS1zO.h5/.84d.TwfkhU6rQbTF.");

        UserProfile profile = new UserProfile();
        profile.setPasswordEncoded(encoder.encode(oldPassword));

        when(userProfileRepository.findUserProfileByClientId(clientId)).thenReturn(profile);
        userProfileService.updatePassword(clientId, newPassword);

        verify(userProfileRepository).save(profile);
    }

    @Test
    public void passwordExists_Matches() {
        String oldPassword = "Pa$$w0rd";
        String encodedPassword = "$2a$10$teBfwK/XgvfczEmnqwAKz.FwEXqS1zO.h5/.84d.TwfkhU6rQbTF.";

        UserProfile userProfile = new UserProfile();
        userProfile.setPasswordEncoded(encodedPassword);

        when(encoder.matches(oldPassword, encodedPassword)).thenReturn(true);

        boolean exists = encoder.matches(oldPassword, encodedPassword);

        assertTrue(exists);
        verify(encoder, times(1)).matches(oldPassword, encodedPassword);
    }

    @Test
    public void passwordExists_NotMatches() {
        String oldPassword = "Pa$$w0rd";
        String encodedPassword = "$2a$10$teBfwK/XgvfczEmnqwAKz.FwEXqS1zO.h5/.84d.TwfkhU6rQbTF.";
        String wrongPassword = "Pa$$w0rd123";

        UserProfile userProfile = new UserProfile();
        userProfile.setPasswordEncoded(encodedPassword);

        when(encoder.matches(oldPassword, encodedPassword)).thenReturn(false);

        boolean exists = encoder.matches(wrongPassword, encodedPassword);

        assertFalse(exists);
        verify(encoder, times(1)).matches(wrongPassword, encodedPassword);
    }

    @Test
    public void updateSecurityQuestion_Success() {
        UUID clientId = UUID.randomUUID();
        String securityQuestion = "What is your favorite color?";
        String securityAnswer = "Black";

        UserProfile userProfile = new UserProfile();
        when(userProfileRepository.findUserProfileByClientId(clientId)).thenReturn(userProfile);

        userProfileService.updateSecurityQuestion(clientId, securityQuestion, securityAnswer);

        verify(userProfileRepository, times(1)).save(userProfile);
    }

    @Test
    public void changeAuthorizationType_whenAuthorizationTypeMatches () {
        UserProfile profile = new UserProfile();
        profile.setAuthorization(AuthorizationType.LOGPASS);
        AuthorizationTypeIncomingDto incomingDto = new AuthorizationTypeIncomingDto(true, false, false);
        String expected = "logpass";

        String actual = userProfileService.changeAuthorizationType(profile, incomingDto).getAuthorization();
        assertEquals(expected, actual);
        verify(userProfileRepository, never()).save(Mockito.any(UserProfile.class));
    }

    @Test
    public void changeAuthorizationType_whenAuthorizationTypeDifferent () {
        UserProfile profile = new UserProfile();
        profile.setAuthorization(AuthorizationType.LOGPASS);
        AuthorizationTypeIncomingDto incomingDto = new AuthorizationTypeIncomingDto(false, true, false);
        when(userProfileRepository.save(profile)).thenReturn(profile);
        String expected = "pincode";

        String actual = userProfileService.changeAuthorizationType(profile, incomingDto).getAuthorization();
        assertEquals(expected, actual);
        verify(userProfileRepository, times(1)).save(profile);
    }

    @Test
    void testSaveFingerprintSuccess() {
        UUID id = UUID.randomUUID();
        String fingerprint = "122112";
        Client client = new Client();
        client.setId(id);
        UserProfile userProfile = new UserProfile();

        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        when(userProfileRepository.findByClientId(id)).thenReturn(Optional.of(userProfile));

        HttpStatus result = userProfileService.saveFingerprint(id, fingerprint);

        verify(userProfileRepository, times(1)).save(userProfile);
        assertEquals(HttpStatus.OK, result);
        assertEquals(fingerprint, userProfile.getFingerprint());
    }

    @Test
    void testSaveFingerprintClientNotFound() {
        UUID id = UUID.randomUUID();
        String fingerprint = "122112";

        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            userProfileService.saveFingerprint(id, fingerprint) );

        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void testSaveFingerprint_UserProfileNotFound() {
        UUID id = UUID.randomUUID();
        String fingerprint = "122112";
        Client client = new Client();
        client.setId(id);

        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        when(userProfileRepository.findByClientId(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            userProfileService.saveFingerprint(id, fingerprint)
        );

        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void authorizationByFingerprint_ValidFingerprint_ReturnsAuthResponseDto() {
        String fingerprint = "122112";
        UUID clientId = UUID.randomUUID();
        String requestId = clientId.toString();
        Client client = new Client();
        client.setId(clientId);
        UserProfile userProfile = new UserProfile();
        userProfile.setClient(client);
        userProfile.setFingerprint(fingerprint);
        AuthResponseDto expectedResponse = new AuthResponseDto(clientId);

        when(clientRepository.findById(UUID.fromString(requestId))).thenReturn(Optional.of(client));
        when(userProfileRepository.findUserProfileByClient(client)).thenReturn(Optional.of(userProfile));

        AuthResponseDto result = userProfileService.authorizationByFingerprint(requestId, fingerprint);

        assertEquals(expectedResponse.getClientId(), result.getClientId());
        verify(clientRepository, times(1)).findById(UUID.fromString(requestId));
        verify(userProfileRepository, times(1)).findUserProfileByClient(client);
    }

    @Test
    void authorizationByFingerprint_ClientNotFound_ThrowsInternalServerException() {
        String fingerprint = "122112";
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        UserProfile userProfile = new UserProfile();
        userProfile.setClient(client);
        userProfile.setFingerprint(fingerprint);

        when(clientRepository.findById(UUID.fromString(client.getId().toString()))).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> userProfileService.authorizationByFingerprint(client.getId().toString(), fingerprint));
        verify(clientRepository, times(1)).findById(UUID.fromString(client.getId().toString()));
        verify(userProfileRepository, never()).findUserProfileByClient(any());
    }

    @Test
    void authorizationByFingerprint_InvalidFingerprint_ThrowsInternalServerException() {
        String fingerprint = "122112";
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        UserProfile userProfile = new UserProfile();
        userProfile.setFingerprint("222222");
        userProfile.setClient(client);

        when(clientRepository.findById(UUID.fromString(clientId.toString()))).thenReturn(Optional.of(client));
        when(userProfileRepository.findUserProfileByClient(client)).thenReturn(Optional.of(userProfile));

        assertThrows(BadRequestException.class, () -> userProfileService.authorizationByFingerprint(clientId.toString(), fingerprint));
    }
}