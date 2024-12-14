package com.example.userservice.web.controller;

import com.example.userservice.app.service.ClientService;
import com.example.userservice.app.service.PassportDataService;
import com.example.userservice.app.service.impl.ContactServiceImpl;
import com.example.userservice.persistence.model.Client;
import com.example.userservice.app.service.impl.UserProfileServiceImpl;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.model.UserProfile;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.NotFoundException;
import com.example.userservice.web.dto.requests.AuthorizationTypeIncomingDto;
import com.example.userservice.web.dto.requests.ClientDto;
import com.example.userservice.web.dto.requests.EmailAndPassportDto;
import com.example.userservice.web.dto.requests.EmailIncomingDto;
import com.example.userservice.web.dto.requests.NotificationStatusDto;
import com.example.userservice.web.dto.requests.PushNotificationsIncomingDto;
import com.example.userservice.web.dto.requests.SecurityQuestionAnswerDto;
import com.example.userservice.web.dto.responses.AuthorizationOutgoingDto;
import com.example.userservice.web.dto.responses.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UsersControllerMvcTest {
    private final static String CORRECT_EMAIL_EXAMPLE = "batman@yandex.ru";
    private final static String INCORRECT_EMAIL_EXAMPLE = " batman@mail.ru";
    private final static String CORRECT_PASSPORT_NUMBER = "1939593943";
    private final static String INCORRECT_PASSPORT_NUMBER = "1939593943jsvnblkknmdkm";
    private final static String CORRECT_MOBILE_PHONE_NUMBER = "71234567890";
    private final static String URL_CHECK_EMAIL_AND_PASSPORT = "/api/v1/users/check-email-passport";
    private final static String URL_CHANGE_EMAIL = "/api/v1/users/settings/email";
    private final static String URL_CHANGE_NOTIFICATION_EMAIL = "/api/v1/users/settings/notification/email";
    private static final String CORRECT_SECURITY_QUESTION = "What is your favorite color?";
    private static final String CORRECT_SECURITY_ANSWER = "Black";
    private static final String INCORRECT_SECURITY_QUESTION = "あなたの好きな色は何ですか？";
    private static final String INCORRECT_SECURITY_ANSWER = "私の好きな色は黒です";
    private static final String WRONG_PASSWORD_MESSAGE = "The password must be no shorter than 6 characters and" +
            " no longer than 20 characters and contain only allowed characters";
    private final static String CORRECT_UPDATE_SECURITY_QUESTION_URL = "/api/v1/users/settings/controls";
    private final static String URL_GET_USER_INFO = "/api/v1/users/info";

    private final static String CLIENT_REGISTRATION = "/api/v1/users";

    private static String pincode = "pincode";
    private final static String URL_CHANGE_AUTHORIZATION_TYPE = "/api/v1/users/settings/login";
    private final static String URL_CHANGE_PUSH_NOTIFICATIONS = "/api/v1/users/settings/notification/push";

    private UUID id;
    private EmailIncomingDto dto;
    private PushNotificationsIncomingDto notificationStatus;
    private Contact contact;
    private EmailAndPassportDto emailAndPassportDto;
    private UserInfoDto userInfoDto;

    private ClientDto clientDto;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ContactServiceImpl contactServiceImpl;
    @MockBean
    private ClientService clientService;
    @MockBean
    private PassportDataService passportDataService;
    @MockBean
    private UserProfileServiceImpl userProfileServiceImpl;


    @SneakyThrows
    @Test
    void changeEmail_whenAllParamsOk_thenReturnStatusOk() {
        id = UUID.randomUUID();
        dto = new EmailIncomingDto(CORRECT_EMAIL_EXAMPLE);

        mockMvc.perform(patch(URL_CHANGE_EMAIL)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(contactServiceImpl, only()).changeEmail(id, dto.getEmail());
    }

    @SneakyThrows
    @Test
    void changeEmail_whenEmailNotValid_thenReturnStatusBadRequest() {
        id = UUID.randomUUID();
        dto = new EmailIncomingDto(INCORRECT_EMAIL_EXAMPLE);

        mockMvc.perform(patch(URL_CHANGE_EMAIL)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(contactServiceImpl, never()).changeEmail(id, dto.getEmail());
    }

    @SneakyThrows
    @Test
    void changePushNotifications_whenIdOrBooleanNotValid_thenReturnStatusBadRequest() {
        id = UUID.randomUUID();
        notificationStatus = new PushNotificationsIncomingDto();
        notificationStatus.setNotificationStatus(null);

        mockMvc.perform(patch(URL_CHANGE_PUSH_NOTIFICATIONS)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationStatus)))
                .andExpect(status().isBadRequest());

        verify(contactServiceImpl, never()).changePushNotifications(any(Contact.class), any(Boolean.class));
    }

    @SneakyThrows
    @Test
    void changePushNotifications_whenContactProfileDoNotExist_thenReturnStatusBadRequest() {
        id = UUID.randomUUID();
        notificationStatus = new PushNotificationsIncomingDto();
        notificationStatus.setNotificationStatus(true);
        when(contactServiceImpl.findContactByClientId(any(UUID.class))).thenThrow(new NotFoundException("The contact profile does not exist"));

        mockMvc.perform(patch(URL_CHANGE_PUSH_NOTIFICATIONS)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationStatus)))
                .andExpect(status().isNotFound());

        verify(contactServiceImpl, never()).changePushNotifications(any(Contact.class), any(Boolean.class));
    }

    @SneakyThrows
    @Test
    void changePushNotifications_whenMethodNotAllowed_thenReturnStatusBadRequest() {
        notificationStatus = new PushNotificationsIncomingDto();
        notificationStatus.setNotificationStatus(true);
        mockMvc.perform(get(URL_CHANGE_PUSH_NOTIFICATIONS)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationStatus)))
                .andExpect(status().isMethodNotAllowed());

        verify(contactServiceImpl, never()).changePushNotifications(any(Contact.class), any(Boolean.class));
    }

    @SneakyThrows
    @Test
    void changePushNotifications_whenAllParamsOk_thenReturnStatusOk() {
        id = UUID.randomUUID();
        notificationStatus = new PushNotificationsIncomingDto();
        notificationStatus.setNotificationStatus(true);
        contact = new Contact();
        contact.setPushNotificationEnable(false);
        when(contactServiceImpl.findContactByClientId(id)).thenReturn(contact);
        doNothing().when(contactServiceImpl).changePushNotifications(contact, notificationStatus.getNotificationStatus());

        mockMvc.perform(patch(URL_CHANGE_PUSH_NOTIFICATIONS)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationStatus)))
                .andExpect(status().isOk());

        verify(contactServiceImpl, times(1)).changePushNotifications(contact, notificationStatus.getNotificationStatus());
    }

    @SneakyThrows
    @Test
    void findPassportAndEmail_whenNormal_thenReturnStatus() {
        emailAndPassportDto = new EmailAndPassportDto(CORRECT_EMAIL_EXAMPLE, CORRECT_PASSPORT_NUMBER);

        when(contactServiceImpl.isExistPassportNumberAndEmail(emailAndPassportDto))
                .thenReturn(false);

        mockMvc.perform(post(URL_CHECK_EMAIL_AND_PASSPORT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailAndPassportDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").isBoolean())
                .andExpect(jsonPath("$.status").value(false));

        verify(contactServiceImpl, times(1)).isExistPassportNumberAndEmail(emailAndPassportDto);
    }

    @SneakyThrows
    @Test
    void findPassportAndEmail_whenEmailIncorrect_thenReturnStatusBadRequest() {
        emailAndPassportDto = new EmailAndPassportDto(INCORRECT_EMAIL_EXAMPLE, CORRECT_PASSPORT_NUMBER);

        mockMvc.perform(post(URL_CHECK_EMAIL_AND_PASSPORT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailAndPassportDto)))
                .andExpect(status().isBadRequest());

        verify(contactServiceImpl, never()).isExistPassportNumberAndEmail(emailAndPassportDto);
    }

    @SneakyThrows
    @Test
    void findPassportAndEmail_whenPassportNumberIncorrect_thenReturnStatusBadRequest() {
        emailAndPassportDto = new EmailAndPassportDto(CORRECT_EMAIL_EXAMPLE, INCORRECT_PASSPORT_NUMBER);

        mockMvc.perform(post(URL_CHECK_EMAIL_AND_PASSPORT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailAndPassportDto)))
                .andExpect(status().isBadRequest());

        verify(contactServiceImpl, never()).isExistPassportNumberAndEmail(emailAndPassportDto);
    }

    @SneakyThrows
    @Test
    void changeNotification_whenAllParamsOk_thenReturnStatusOk() {
        UUID id = UUID.randomUUID();
        NotificationStatusDto dto = new NotificationStatusDto(true);
        boolean expected = dto.getNotificationStatus();
        Contact contact = Contact.builder()
                .email(CORRECT_EMAIL_EXAMPLE)
                .client(new Client())
                .emailNotificationEnable(!expected)
                .mobilePhone(CORRECT_MOBILE_PHONE_NUMBER)
                .build();

        when(contactServiceImpl.findContactByClientId(id)).thenReturn(contact);

        mockMvc.perform(patch(URL_CHANGE_NOTIFICATION_EMAIL)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        assertEquals(expected, contact.getEmailNotificationEnable());
        verify(contactServiceImpl, atLeastOnce()).save(contact);
    }

    @SneakyThrows
    @Test
    void changeNotification_whenContactEmailNull_thenReturnStatusBadRequest() {
        UUID id = UUID.randomUUID();
        NotificationStatusDto dto = new NotificationStatusDto(true);
        boolean expected = !dto.getNotificationStatus();
        Contact contact = Contact.builder()
                .email(null)
                .client(new Client())
                .emailNotificationEnable(expected)
                .mobilePhone(CORRECT_MOBILE_PHONE_NUMBER)
                .build();

        when(contactServiceImpl.findContactByClientId(id)).thenReturn(contact);

        mockMvc.perform(patch(URL_CHANGE_NOTIFICATION_EMAIL)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        assertEquals(expected, contact.getEmailNotificationEnable());
        verify(contactServiceImpl, never()).save(contact);
    }

    @SneakyThrows
    @Test
    void changeNotification_whenNotificationStatusAlreadyEnabled_thenReturnStatusBadRequest() {
        UUID id = UUID.randomUUID();
        NotificationStatusDto dto = new NotificationStatusDto(true);
        boolean expected = dto.getNotificationStatus();
        Contact contact = Contact.builder()
                .email(CORRECT_EMAIL_EXAMPLE)
                .client(new Client())
                .emailNotificationEnable(expected)
                .mobilePhone(CORRECT_MOBILE_PHONE_NUMBER)
                .build();

        when(contactServiceImpl.findContactByClientId(id)).thenReturn(contact);

        mockMvc.perform(patch(URL_CHANGE_NOTIFICATION_EMAIL)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(contactServiceImpl, never()).save(contact);
    }

    @SneakyThrows
    @Test
    void updateSecurityQuestion_Success() {
        UUID id = UUID.randomUUID();
        SecurityQuestionAnswerDto securityQuestionAnswerDto = new SecurityQuestionAnswerDto(CORRECT_SECURITY_QUESTION, CORRECT_SECURITY_ANSWER);

        mockMvc.perform(patch(CORRECT_UPDATE_SECURITY_QUESTION_URL)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(securityQuestionAnswerDto)))
                .andExpect(status().isOk());

        verify(userProfileServiceImpl, times(1)).updateSecurityQuestion(
                id, securityQuestionAnswerDto.getSecurityQuestion(), securityQuestionAnswerDto.getSecurityAnswer());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("securityQuestionInvalidDtos")
    void updateSecurityQuestion_WhenInvalidParameters_BadRequest(SecurityQuestionAnswerDto incomingDto) {
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch(CORRECT_UPDATE_SECURITY_QUESTION_URL)
                        .header("ClientId", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingDto)))
                .andExpect(status().isBadRequest());

        verify(userProfileServiceImpl, never()).updateSecurityQuestion(id, incomingDto.getSecurityQuestion(), incomingDto.getSecurityAnswer());
    }

    private static Stream<SecurityQuestionAnswerDto> securityQuestionInvalidDtos() {
        return Stream.of(
                new SecurityQuestionAnswerDto(INCORRECT_SECURITY_QUESTION, CORRECT_SECURITY_ANSWER),
                new SecurityQuestionAnswerDto(CORRECT_SECURITY_QUESTION, INCORRECT_SECURITY_ANSWER),
                new SecurityQuestionAnswerDto("", CORRECT_SECURITY_ANSWER),
                new SecurityQuestionAnswerDto(CORRECT_SECURITY_QUESTION, "")
        );
    }

    @Test
    @SneakyThrows
    void changeAuthorizationType_whenIncomingDtoIncorrect_thenThrowValidationException() {
        AuthorizationTypeIncomingDto incomingDto = new AuthorizationTypeIncomingDto(true, true, true);
        id = UUID.randomUUID();

        mockMvc.perform(patch(URL_CHANGE_AUTHORIZATION_TYPE)
                        .param(id.toString(), "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingDto)))
                .andExpect(status().isBadRequest());

        verify(userProfileServiceImpl, never()).changeAuthorizationType(any(UserProfile.class),
                any (AuthorizationTypeIncomingDto.class));
    }

    @Test
    @SneakyThrows
    void changeAuthorizationType_whenIncomingDtoCorrect_thenReturnOk() {
        AuthorizationTypeIncomingDto incomingDto = new AuthorizationTypeIncomingDto(false, true, false);
        id = UUID.randomUUID();
        UserProfile profile = new UserProfile();
        AuthorizationOutgoingDto outgoingDto = new AuthorizationOutgoingDto("pincode");
        when(userProfileServiceImpl.findUserProfileByClientId(id)).thenReturn(profile);
        when(userProfileServiceImpl.changeAuthorizationType(profile, incomingDto)).thenReturn(outgoingDto);

        mockMvc.perform(patch(URL_CHANGE_AUTHORIZATION_TYPE)
                        .header("ClientId", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingDto)))
                .andExpect(status().isOk());

        verify(userProfileServiceImpl, times(1)).changeAuthorizationType(any(UserProfile.class),
                any (AuthorizationTypeIncomingDto.class));
        verify(userProfileServiceImpl, times(1)).findUserProfileByClientId(id);
    }

    @Test
    @SneakyThrows
    void changeAuthorizationType_whenProfileNotFound_thenThrowBadRequestException() {
        AuthorizationTypeIncomingDto incomingDto = new AuthorizationTypeIncomingDto(false, true, false);
        id = UUID.randomUUID();
        UserProfile profile = new UserProfile();
        AuthorizationOutgoingDto outgoingDto = new AuthorizationOutgoingDto(pincode);
        when(userProfileServiceImpl.changeAuthorizationType(profile, incomingDto)).thenReturn(outgoingDto);
        doThrow(BadRequestException.class).when(userProfileServiceImpl).findUserProfileByClientId(id);

        mockMvc.perform(patch(URL_CHANGE_AUTHORIZATION_TYPE)
                        .header("ClientId", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingDto)))
                .andExpect(status().isBadRequest());

        verify(userProfileServiceImpl, never()).changeAuthorizationType(any(UserProfile.class),
                any (AuthorizationTypeIncomingDto.class));
        verify(userProfileServiceImpl, times(1)).findUserProfileByClientId(id);
    }

    @SneakyThrows
    @Test
    void getUserInfoDto_whenNormal_thenReturnUserInfoDto() {
        id = UUID.randomUUID();
        userInfoDto = new UserInfoDto("Александр", "Македонский", "Степанович",
                CORRECT_EMAIL_EXAMPLE, "89329995533", CORRECT_PASSPORT_NUMBER);

        when(clientService.getUserInfoById(id)).thenReturn(userInfoDto);

        mockMvc.perform(get(URL_GET_USER_INFO)
                        .header("ClientId", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(userInfoDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userInfoDto.getLastName()))
                .andExpect(jsonPath("$.surname").value(userInfoDto.getSurname()))
                .andExpect(jsonPath("$.passportNumber").value(userInfoDto.getPassportNumber()))
                .andExpect(jsonPath("$.mobilePhone").value(userInfoDto.getMobilePhone()))
                .andExpect(jsonPath("$.email").value(userInfoDto.getEmail()));
    }

    @Test
    @SneakyThrows
    void getUserInfoDto_whenNotFound_throwNotFoundException() {
        id = UUID.randomUUID();

        doThrow(new NotFoundException("Client Not Found")).when(clientService).getUserInfoById(id);

        mockMvc.perform(get(URL_GET_USER_INFO)
                        .header("ClientId", id))
                .andExpect(status().isNotFound());
    }


    @Test
    @SneakyThrows
    void clientRegistration_notValidPassword_throwInvalidPassword() {
        clientDto = new ClientDto(UUID.randomUUID(), "79370458234", "bob.millera@gmail.com",
                "12345qwerty", "what is your pet name?", "barsik");

        mockMvc.perform(post(CLIENT_REGISTRATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(WRONG_PASSWORD_MESSAGE));
    }
}