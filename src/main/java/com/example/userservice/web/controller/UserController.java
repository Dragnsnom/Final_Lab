package com.example.userservice.web.controller;

import com.example.userservice.app.service.ClientService;
import com.example.userservice.app.service.ContactService;
import com.example.userservice.app.service.PassportDataService;
import com.example.userservice.app.service.UserProfileService;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.model.UserProfile;
import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.dto.errors.ErrorResponseDTO;
import com.example.userservice.web.dto.requests.AuthorizationTypeIncomingDto;
import com.example.userservice.web.dto.requests.ClientDto;
import com.example.userservice.web.dto.requests.EmailAndPassportDto;
import com.example.userservice.web.dto.requests.EmailIncomingDto;
import com.example.userservice.web.dto.requests.FingerprintDTO;
import com.example.userservice.web.dto.requests.NonClientDto;
import com.example.userservice.web.dto.requests.NotificationStatusDto;
import com.example.userservice.web.dto.requests.PasswordChangeDTO;
import com.example.userservice.web.dto.requests.PushNotificationsIncomingDto;
import com.example.userservice.web.dto.requests.SecurityQuestionAnswerDto;
import com.example.userservice.web.dto.responses.AuthResponseDto;
import com.example.userservice.web.dto.responses.AuthorizationOutgoingDto;
import com.example.userservice.web.dto.responses.EmailAndPassportResponseDto;
import com.example.userservice.web.dto.responses.MobilePhoneDTO;
import com.example.userservice.web.dto.responses.NotificationsInfoDto;
import com.example.userservice.web.dto.responses.PassportNumberDto;
import com.example.userservice.web.dto.responses.PasswordDTO;
import com.example.userservice.web.dto.responses.RegistrationInfoDTO;
import com.example.userservice.web.dto.responses.UserInfoDto;
import com.example.userservice.web.util.RequestValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.userservice.web.util.RequestValidator.authorizationTypeValidate;

@RestController
@RequestMapping(path = "api/v1/users", produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "UserController", description = "Работает с пользовательскими данными")
public class UserController {

    private final ClientService clientService;

    private final PassportDataService passportDataService;

    private final UserProfileService userProfileService;

    private final ContactService contactService;

    @Operation(
            summary = "Проверка регистрации пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "В случае успешного сценария (номер телефона не найден / номер телефона " +
                                    "найден, идентификатор клиента найден)",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RegistrationInfoDTO.class)
                            )
                    ),

                    @ApiResponse(
                            responseCode = "404",
                            description = " Если не удалось обнаружить указанный URL",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),

            }
    )
    @GetMapping("/status")
    public ResponseEntity<RegistrationInfoDTO> checkRegistration(@RequestParam String mobilePhone) {
        try {
            mobilePhone = RequestValidator.mobilePhoneValidate(mobilePhone);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(exception.getMessage());
        }

        RegistrationInfoDTO responseDTO = clientService.findByMobilePhone(mobilePhone);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение номера телефона по паспорту",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "В случае нахождения данных",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MobilePhoneDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "В случае отсутствия данных",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/phone")
    public ResponseEntity<MobilePhoneDTO> getPhoneNumberByPassportNumber(@RequestBody PassportNumberDto passportNumberDto) {
        String passport = RequestValidator.passportNumberValidate(passportNumberDto.getPassportNumber());

        MobilePhoneDTO phoneNumberByPassportNumber = passportDataService.getPhoneNumberByPassportNumber(passport);
        return new ResponseEntity<>(phoneNumberByPassportNumber, HttpStatus.OK);
    }

    @Operation(
            summary = "Восстановление пароля",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "В случаях успешной записи данных в verification "
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = " В случае неуспешной записи пароля в таблицу User_Profile",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "В случае отсутствия db_user_profile или db_contacts у клиента",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PatchMapping("/reset")
    public HttpStatus resetPassword(@RequestHeader UUID clientId,
                                    @RequestBody @Valid PasswordDTO passwordDTO) {
        return userProfileService.resetPassword(clientId, passwordDTO.getPassword());
    }

    @Operation(
            summary = "Changing e-mail address of user",
            responses = {

                    @ApiResponse(
                            responseCode = "404",
                            description = " Если не удалось обнаружить указанный URL",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),

                    @ApiResponse(
                            responseCode = "409",
                            description = "В случае, если поле newEmail содержит уже существующее значение у данного или другого пользователя",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))})

    @PatchMapping("/settings/email")
    @ResponseStatus(HttpStatus.OK)
    public void changeEmail(@RequestHeader UUID clientId,
                            @Valid @RequestBody EmailIncomingDto emailIncomingDto) {
        log.info("Changing e-mail for user with ID = {}, by = {}", clientId, emailIncomingDto.getEmail());
        contactService.changeEmail(clientId, emailIncomingDto.getEmail());
    }

    @Operation(
            summary = "Проверка номера паспорта и email при регистрации",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Результат проверки нахождения данных в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = EmailAndPassportResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping("/check-email-passport")
    public ResponseEntity<EmailAndPassportResponseDto> findPassportAndEmail(@Valid @RequestBody
                                                                            EmailAndPassportDto emailAndPassportDto) {
        EmailAndPassportResponseDto responseDto = new EmailAndPassportResponseDto();
        responseDto.setStatus(contactService.isExistPassportNumberAndEmail(emailAndPassportDto));

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Смена пароля в профиле",
            responses = {
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не удалось обнаружить указанный URL",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/settings/password")
    public void updatedPassword(@RequestHeader UUID clientId, @Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        String oldPassword = passwordChangeDTO.getOldPassword();
        String newPassword = passwordChangeDTO.getNewPassword();

        if (userProfileService.passwordExists(clientId, oldPassword)) {
            userProfileService.updatePassword(clientId, newPassword);
        } else {
            throw new BadRequestException("Incorrect password");
        }
    }

    @Operation(
            summary = "Изменение PUSH-уведомлений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешная запись данных в таблицу db_contacts"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неуспешная валидации токена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Невалидный URL",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            ))})
    @PatchMapping("/settings/notification/push")
    public HttpStatus changePushNotifications(@RequestHeader UUID clientId,
                                              @Valid @RequestBody PushNotificationsIncomingDto notificationStatus) {
        Contact contact = contactService.findContactByClientId(clientId);

        if (contact.getPushNotificationEnable().equals(notificationStatus.getNotificationStatus())) {
            return HttpStatus.OK;
        }

        log.info("Changing push notification status for user with ID = {}, to = {}", clientId, notificationStatus.getNotificationStatus());
        contactService.changePushNotifications(contact, notificationStatus.getNotificationStatus());
        return HttpStatus.OK;
    }

    @PatchMapping("/settings/controls")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Смена контрольного вопроса/ответа в профиле",
            responses = {
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не удалось обнаружить указанный URL",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    public void updateSecurityQuestion(@RequestHeader UUID clientId, @Valid @RequestBody SecurityQuestionAnswerDto securityQADto) {
        String securityQuestion = securityQADto.getSecurityQuestion();
        String securityAnswer = securityQADto.getSecurityAnswer();

        userProfileService.updateSecurityQuestion(clientId, securityQuestion, securityAnswer);
    }

    @Operation(
            summary = "Регистрация не клиента",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "В случае успешной регистрации"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Если при создании записей ошибки",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если были переданы не валидные данные",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public void nonClientRegistration(@RequestBody @Valid NonClientDto nonClientDto) {
        RequestValidator.nonClientDtoValidate(nonClientDto);
        clientService.nonClientRegistration(nonClientDto);
    }

    @Operation(
            summary = "Регистрация клиента",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "В случае успешной регистрации"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Если при создании записей ошибки",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если были переданы не валидные данные",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void clientRegistration(@RequestBody @Valid ClientDto clientDto) {
        RequestValidator.clientDtoValidate(clientDto);
        clientService.clientRegistration(clientDto);
    }

    @Operation(
            summary = "Changing notifications by e-mail address of user",
            responses = {

                    @ApiResponse(
                            responseCode = "404",
                            description = "Не удалось обнаружить указанный URL",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))})

    @PatchMapping("/settings/notification/email")
    @ResponseStatus(HttpStatus.OK)
    public void changeNotification(@RequestHeader UUID clientId,
                                   @Valid @RequestBody NotificationStatusDto notificationStatusDto) {

        boolean requiredStatus = notificationStatusDto.getNotificationStatus();
        Contact contact = contactService.findContactByClientId(clientId);
        String email = contact.getEmail();
        boolean currentStatus = contact.getEmailNotificationEnable();

        log.info("Changing notification for contact = {}, with email = {}, with status ={} by status = {}",
                contact, email, currentStatus, requiredStatus);

        if (email != null && currentStatus != requiredStatus) {
            contact.setEmailNotificationEnable(requiredStatus);
            contactService.save(contact);
        } else {
            log.error("Can't change notification for contact = {} due to invalid parameters", contact);
            throw new BadRequestException(String.format("Can't change notification for contact = %s, " +
                    "with email = %s, with status = %s by status = %s", contact, email, currentStatus, requiredStatus));
        }
    }

    @Operation(
            summary = "Изменение способа авторизации",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешная запись данных в таблицу db_user_profile"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неуспешная валидация токена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Невалидный URL",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            ))})
    @PatchMapping("/settings/login")
    public ResponseEntity<AuthorizationOutgoingDto> changeAuthorizationType(@RequestHeader UUID clientId,
                                                                            @Valid @RequestBody AuthorizationTypeIncomingDto dto) {
        if (!authorizationTypeValidate(dto)) {
            throw new ValidationException("Only one authorization type field must be true. " + dto.toString());
        }

        UserProfile profile = userProfileService.findUserProfileByClientId(clientId);

        log.info("Changing authorization type for user with ID = {}", clientId);
        AuthorizationOutgoingDto outgoingDto = userProfileService.changeAuthorizationType(profile, dto);
        return new ResponseEntity<>(outgoingDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение информации о пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешная выдача данных из таблиц db_contacts, db_passport_data, " +
                                    "db_client"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Отсутствие или не корректность входного параметра",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не указаны или не действительны данные аутентификации",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Невалидный URL",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "405",
                            description = "Метод запроса не соответствует GET",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            ))})
    @GetMapping("/info")
    public UserInfoDto getInfoByUser(@RequestHeader("ClientId") UUID clientId) {
        return clientService.getUserInfoById(clientId);
    }

    @Operation(
            summary = "Отправка настроек уведомлений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "В случае успешного поиска",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = NotificationsInfoDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "В случае неуспешного поиска",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/settings/notifications/all")
    public NotificationsInfoDto getNotificationsInfo(@RequestHeader UUID clientId) {
        return contactService.getNotificationsInfoByClientId(clientId);
    }

    @Operation(
            summary = "Изменение SMS-уведомлений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "В случаях успешной записи данных в таблицу db_contacts"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если ничего не было передано или переданы невалидные данные",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не удалось обнаружить указанный URL",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))})

    @PatchMapping("/settings/notification/sms")
    public void changeSmsNotification(@RequestHeader UUID clientId,
                                      @Valid @RequestBody NotificationStatusDto notificationStatusDto) {

        contactService.changeSmsNotifications(clientId, notificationStatusDto.getNotificationStatus());
    }

    @Operation(
            summary = "Сохранение PIN-кода для входа с мобильных устройств в бд",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "В случаях успешной записи данных в таблицу db_user_profile"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если ничего не было передано или переданы невалидные данные",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/login/fingerprint")
    public HttpStatus saveFingerprint(@RequestHeader UUID clientId,
                                      @Valid @RequestBody FingerprintDTO fingerprintDTO) {
        String fingerprint = RequestValidator.fingerprintValidate(fingerprintDTO.getFingerprint());

        return userProfileService.saveFingerprint(clientId, fingerprint);
    }

    @Operation(
            summary = "Авторизация с помощью PIN-кода с мобильных устройств",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "В случаях успешной совпадения введенных данных с данными в db_user_profile",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если переданы невалидные данные, или переданные данные не соответствуют данным в бд db_user_profile",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/login/pin")
    public ResponseEntity<AuthResponseDto> authorizationByFingerprint(@RequestHeader String clientId,
                                                                      @RequestBody FingerprintDTO fingerprintDTO) {
        String fingerprint = RequestValidator.fingerprintValidate(fingerprintDTO.getFingerprint());

        AuthResponseDto response = userProfileService.authorizationByFingerprint(clientId, fingerprint);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
