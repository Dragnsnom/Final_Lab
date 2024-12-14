package com.example.userservice.web.controller;

import com.example.userservice.app.enums.ClientStatus;
import com.example.userservice.app.service.impl.ClientServiceImpl;
import com.example.userservice.app.service.impl.ContactServiceImpl;
import com.example.userservice.app.service.impl.UserProfileServiceImpl;
import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.UserProfile;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.dto.requests.AuthorizationTypeIncomingDto;
import com.example.userservice.web.dto.requests.ClientDto;
import com.example.userservice.web.dto.requests.FingerprintDTO;
import com.example.userservice.web.dto.requests.NonClientDto;
import com.example.userservice.web.dto.requests.NotificationStatusDto;
import com.example.userservice.web.dto.requests.PasswordChangeDTO;
import com.example.userservice.web.dto.responses.AuthResponseDto;
import com.example.userservice.web.dto.responses.AuthorizationOutgoingDto;
import com.example.userservice.web.dto.responses.RegistrationInfoDTO;
import com.example.userservice.web.util.RequestValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserControllerTest {

    @Mock
    private ClientServiceImpl clientServiceImpl;

    @Mock
    private UserProfileServiceImpl userProfileService;

    @Mock
    private ContactServiceImpl contactService;

    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private UserController userController;

    @Test
    void checkRegistrationWithValidParametersTest() {
        RegistrationInfoDTO registrationInfoResponse = new RegistrationInfoDTO("79370458234",
                ClientStatus.NOT_ACTIVE.name());
        registrationInfoResponse.setClientId(UUID.randomUUID().toString());

        when(clientServiceImpl.findByMobilePhone(Mockito.anyString())).thenReturn(registrationInfoResponse);

        ResponseEntity<RegistrationInfoDTO> responseEntity = userController.checkRegistration("79370458234");

        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseEntity.getBody(), registrationInfoResponse);
    }

    @Test
    void checkRegistrationWithInvalidParametersTest() {
        when(clientServiceImpl.findByMobilePhone(Mockito.anyString())).thenThrow(BadRequestException.class);

        assertThrows(BadRequestException.class, () -> userController.checkRegistration("79370458234"));
    }

    @Test
    void updatePassword_Success() {
        UUID clientId = UUID.randomUUID();
        String oldPassword = "Pa$$w0rd";
        String newPassword = "Pa$$w0rd123";

        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO(oldPassword, newPassword);

        when(userProfileService.passwordExists(clientId, oldPassword)).thenReturn(true);

        userController.updatedPassword(clientId, passwordChangeDTO);
        assertTrue(userProfileService.passwordExists(clientId, oldPassword));

        verify(userProfileService).updatePassword(clientId, newPassword);
    }

    @Test
    void updatePassword_IncorrectPassword() {
        UUID clientId = UUID.randomUUID();
        String oldPassword = "Pa$$w0rd";
        String newPassword = "Pa$$w0rd123";

        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO(oldPassword, newPassword);

        when(userProfileService.passwordExists(clientId, oldPassword)).thenReturn(false);

        try {
            userController.updatedPassword(clientId, passwordChangeDTO);
        } catch (BadRequestException e) {
            assertEquals("Incorrect password", e.getMessage());
        }

        verify(userProfileService, times(1)).passwordExists(clientId, oldPassword);
        verifyNoMoreInteractions(userProfileService);
    }

    @Test
    void updatePassword_InvalidNewPassword() {
        UUID clientId = UUID.randomUUID();
        String oldPassword = "oldPassword";
        String newPassword = "weakpass";

        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO(oldPassword, newPassword);

        assertThrows(BadRequestException.class, () -> userController.updatedPassword(clientId, passwordChangeDTO));
        verify(userProfileService, never()).updatePassword(clientId, newPassword);
    }

    @Test
    void nonClientRegistration_InvalidFirstName() {
        NonClientDto nonClientDtoInvalidFirstName = new NonClientDto("J", "Snow", "RUS",
                "79370458234", "john.snow@gmail.com", "AB33455712",
                LocalDate.of( 2019 , 05, 20 ),
                "Russian federation", LocalDate.of( 2029 , 05, 20 ),
                LocalDate.of( 1985 , 05, 21 ),
                "ABC123abc/", "what do you know?", "nothing"
        );

        assertThrows(ValidationException.class, () -> userController.nonClientRegistration(nonClientDtoInvalidFirstName));
        verify(clientServiceImpl, never()).nonClientRegistration(nonClientDtoInvalidFirstName);
    }

    @Test
    void nonClientRegistration_Success() {
        NonClientDto nonClientDto = new NonClientDto("Jon", "Snow", "RUS",
                "79370458234", "john.snow@gmail.com", "AB33455712",
                LocalDate.of( 2019 , 05, 20 ),
                "Russian federation", LocalDate.of( 2029 , 05, 20 ),
                LocalDate.of( 1985 , 05, 21 ),
                "ABC123abc/", "what do you know?", "nothing"
        );

        doNothing().when(clientServiceImpl).nonClientRegistration(nonClientDto);
        userController.nonClientRegistration(nonClientDto);
        verify(clientServiceImpl).nonClientRegistration(nonClientDto);
    }

    @Test
    void clientRegistration_Success() {
        ClientDto clientDto = new ClientDto(UUID.randomUUID(), "79370458234", "bob.millera@gmail.com",
                "ABC123abc/", "what is your pet name?", "barsik");

        doNothing().when(clientServiceImpl).clientRegistration(clientDto);
        userController.clientRegistration(clientDto);
        verify(clientServiceImpl).clientRegistration(clientDto);
    }

    @Test
    void getNotificationsInfo_Success(){
        UUID id = UUID.randomUUID();
        userController.getNotificationsInfo(id);
        verify(contactService).getNotificationsInfoByClientId(id);
    }

    @Test
    void changeSmsNotification_Success() {
        UUID id = UUID.randomUUID();
        NotificationStatusDto notificationStatusDto = new NotificationStatusDto(false);
        userController.changeSmsNotification(id, notificationStatusDto);
        verify(contactService).changeSmsNotifications(id, notificationStatusDto.getNotificationStatus());
    }
    @Test
    public void testSaveFingerprintReturnsHttpStatusOk() {
        UUID clientId = UUID.randomUUID();
        FingerprintDTO fingerprintDTO = new FingerprintDTO();
        fingerprintDTO.setFingerprint("122112");

        when(userProfileService.saveFingerprint(clientId, fingerprintDTO.getFingerprint())).thenReturn(HttpStatus.OK);

        HttpStatus result = userController.saveFingerprint(clientId, fingerprintDTO);

        assertEquals(HttpStatus.OK, result);
        verify(userProfileService).saveFingerprint(clientId, fingerprintDTO.getFingerprint());

    }

    @Test
    void saveFingerprint_InCorrectFingerprint() {
        UUID clientId = UUID.randomUUID();
        FingerprintDTO fingerprintDTO = new FingerprintDTO();
        fingerprintDTO.setFingerprint("12211 ");

        assertThrows(ValidationException.class, () -> userController.saveFingerprint(clientId, fingerprintDTO));
        verify(userProfileService, never()).saveFingerprint(clientId, fingerprintDTO.getFingerprint());
    }

    @Test
    void authByPin() {
        UUID clientId = UUID.randomUUID();
        FingerprintDTO fingerprintDTO = new FingerprintDTO();
        fingerprintDTO.setFingerprint("122112");
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setClientId(clientId);

        when(userProfileService.authorizationByFingerprint(clientId.toString(), fingerprintDTO.getFingerprint())).thenReturn(authResponseDto);

        ResponseEntity<AuthResponseDto> responseEntity = userController.authorizationByFingerprint(clientId.toString(), fingerprintDTO);

        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseEntity.getBody(), authResponseDto);

    }

    @Test
    void testChangeAuthorizationType() {
        UUID clientId = UUID.randomUUID();
        AuthorizationTypeIncomingDto dto = new AuthorizationTypeIncomingDto();
        dto.setLogpass(false);
        dto.setBiometrics(true);
        dto.setPincode(false);

        Client client = new Client();
        client.setId(clientId);

        UserProfile userProfile = new UserProfile();
        userProfile.setClient(client);
        userProfile.setSecurityAnswer("test");
        userProfile.setSecurityQuestion("test");

        when(userProfileService.findUserProfileByClientId(clientId)).thenReturn(userProfile);

        ResponseEntity<AuthorizationOutgoingDto> response = userController.changeAuthorizationType(clientId, dto);

        verify(userProfileService, times(1)).findUserProfileByClientId(clientId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testChangeAuthorizationTypeWithInvalidDto() {
        UUID clientId = UUID.randomUUID();
        AuthorizationTypeIncomingDto dto = new AuthorizationTypeIncomingDto();
        dto.setLogpass(false);
        dto.setBiometrics(true);
        dto.setPincode(true);

        Client client = new Client();
        client.setId(clientId);

        UserProfile userProfile = new UserProfile();
        userProfile.setClient(client);
        userProfile.setSecurityAnswer("test");
        userProfile.setSecurityQuestion("test");

        when(userProfileService.findUserProfileByClientId(clientId)).thenReturn(userProfile);

        assertThrows(ValidationException.class, () -> {
            userController.changeAuthorizationType(clientId, dto);
        });
    }
}