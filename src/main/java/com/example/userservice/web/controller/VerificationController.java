package com.example.userservice.web.controller;

import com.example.userservice.app.service.VerificationService;
import com.example.userservice.web.dto.errors.ErrorResponseDTO;
import com.example.userservice.web.dto.requests.VerifyByMobilePhoneAndVerificationCodeDTO;
import com.example.userservice.web.dto.responses.*;
import com.example.userservice.web.dto.requests.VerifyByPassportNumberAndVerificationCodeDTO;
import com.example.userservice.web.util.RequestValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/verifications", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "VerificationController", description = "Работает с кодом верификации")
public class VerificationController {

    private final VerificationService verificationService;

    @Operation(
            summary = "Генерация кода верификации по номеру телефона",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "В случаях успешной записи данных",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HttpStatus.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если ничего не было передано или переданы не валидные данные",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Если в таблице db_contacts нет записи с таким номером",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400 BAD REQUEST + оставшееся количество секунд блокировки",
                            description = "Если пользователь был заблокирован, и срок блокировки не истек",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<HttpStatus> generateCodeByMobilePhone(@RequestBody MobilePhoneDTO mobilePhoneDto) {
        String mobilePhone = RequestValidator.mobilePhoneValidate(mobilePhoneDto.getMobilePhone());
        verificationService.generateCodeByMobilePhone(mobilePhone);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Верификация пользователя по номеру телефона с учетом блокировки пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Если переданный код верификации соответствует коду верификации в БД"
                    ),
                    @ApiResponse(
                            responseCode = "403 + оставшееся количество секунд блокировки",
                            description = "Если срок блокировки не истек, или блокируем пользователя",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Номер телефона не найден в базе данных db_contacts, или срок блокировки истек, либо код верификации не был создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если ничего не было передано или переданы не валидные данные, либо введен неверный код верификации, но еще есть попытки для ввода",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping(path = "/verify")
    public ResponseEntity<HttpStatus> verifyByMobilePhoneAndVerificationCode(
            @RequestBody VerifyByMobilePhoneAndVerificationCodeDTO verifyDto) {
        verificationService.verifyByMobilePhoneAndVerificationCode(
                verifyDto.getMobilePhone(),
                verifyDto.getVerificationCode()
        );

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Верификация пользователя по номеру телефона с учетом блокировки пользователя и дальнейшей авторизации",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Если переданный код верификации соответствует коду верификации в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403 + оставшееся количество секунд блокировки",
                            description = "Если срок блокировки не истек, или блокируем пользователя",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Номер телефона не найден в базе данных db_contacts, или срок блокировки истек, либо код верификации не был создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если ничего не было передано или переданы не валидные данные, либо введен неверный код верификации, но еще есть попытки для ввода",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping(path = "/verify/phone")
    public ResponseEntity<AuthResponseDto> verifyByMobilePhoneAndVerificationCodeWithAuth(
            @RequestBody VerifyByMobilePhoneAndVerificationCodeDTO verifyDto) {
        AuthResponseDto authResponseDto = verificationService.verifyByMobilePhoneAndVerificationCodeWithAuth(
                verifyDto.getMobilePhone(),
                verifyDto.getVerificationCode()
        );

        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Генерация кода верификации на основе номера паспорта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "В случаях успешной записи данных",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HttpStatus.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если ничего не было передано или переданы не валидные данные",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Если в db_contacts нет записи, связанной с владельцем паспорта",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400 BAD REQUEST + оставшееся количество секунд блокировки",
                            description = "Если пользователь был заблокирован, и срок блокировки не истек",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/generate")
    public ResponseEntity<HttpStatus> generateCodeByPassportNumber(@RequestBody PassportNumberDto passportNumberDto) {
        String passportNumber = RequestValidator.passportNumberValidate(passportNumberDto.getPassportNumber());
        verificationService.generateCodeByPassportNumber(passportNumber);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/verify/passport")
    @Operation(
            summary = "Верификация пользователя по номеру паспорта с учетом блокировки пользователя и дальнейшей авторизации",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Если переданный код верификации соответствует коду верификации в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403 + оставшееся количество секунд блокировки",
                            description = "Если срок блокировки не истек, или блокируем пользователя",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Номер телефона не найден в базе данных db_contacts, или срок блокировки истек, либо код верификации не был создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если ничего не было передано или переданы не валидные данные, либо введен неверный код верификации, но еще есть попытки для ввода",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    )
            }
    )
    public ResponseEntity<AuthResponseDto> verifyByPassportNumberAndVerificationCodeWithAuth(
            @RequestBody VerifyByPassportNumberAndVerificationCodeDTO verifyDto) {
        AuthResponseDto authResponseDto = verificationService.verifyByPassportNumberAndVerificationCodeWithAuth(
                verifyDto.getPassportNumber(),
                verifyDto.getVerificationCode()
        );
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

}
