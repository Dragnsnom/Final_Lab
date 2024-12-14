package com.example.userservice.web.util;

import com.example.userservice.web.dto.requests.AuthorizationTypeIncomingDto;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;

import static com.example.userservice.web.util.RequestValidator.authorizationTypeValidate;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestValidatorTest {

    private static final String WITHOUT_DOMAIN_EMAIL = "..lksklkeng@lklskvmlskf.";
    private static final String INCORRECT_SHORT_EMAIL = "abc@hgn.com";
    private static final String INCORRECT_SYMBOL_EMAIL = "a(bc@hgn.com";
    private static final String WITHOUT_AT_EMAIL = "abchgn.com";
    private static final String CORRECT_EMAIL = "correct@rambler.ru";

    @Test
    void validateMobilePhoneTest() {
        String rightMobilePhone = "79237456139";

        String mobilePhoneWithSpaces = "7 923 745 61 39";
        String mobilePhoneStartWith8 = "89237456139";
        String mobilePhoneWrongLength = "892374561";
        String mobilePhoneWithLetters = "79u3745613R";

        assertEquals(rightMobilePhone, RequestValidator.mobilePhoneValidate(rightMobilePhone));
        assertEquals(rightMobilePhone, RequestValidator.mobilePhoneValidate(mobilePhoneWithSpaces));
        assertEquals(rightMobilePhone, RequestValidator.mobilePhoneValidate(mobilePhoneStartWith8));

        assertThrows(ValidationException.class, () -> RequestValidator.mobilePhoneValidate(mobilePhoneWrongLength));
        assertThrows(ValidationException.class, () -> RequestValidator.mobilePhoneValidate(mobilePhoneWithLetters));
    }

    @Test
    void verificationCodeValidateTest() {
        String verificationCode = "123456";

        String verificationCodeWithSpaces = "1 2 3 4 5 6";
        String verificationCodeWrongLength = "1234";
        String verificationCodeWithLetters = "I2345R";

        assertEquals(verificationCode, RequestValidator.verificationCodeValidate(verificationCode));
        assertEquals(verificationCode, RequestValidator.verificationCodeValidate(verificationCodeWithSpaces));

        assertThrows(ValidationException.class, () ->
                RequestValidator.verificationCodeValidate(verificationCodeWithLetters));
        assertThrows(ValidationException.class, () ->
                RequestValidator.verificationCodeValidate(verificationCodeWrongLength));
    }

    @Test
    void passportNumberValidate() {
        String correctPassportNumber = "123M 3543K";
        String wrongPassportNumber = "МУТавмот ыва";
        String firstSpacePassportNumber = " 123M3543";
        String onlySymbolNumber = "FHTK CHFJK";

        assertThrows(ValidationException.class, () -> RequestValidator.passportNumberValidate(onlySymbolNumber));
        assertThrows(ValidationException.class, () -> RequestValidator.passportNumberValidate(wrongPassportNumber));
        assertThrows(ValidationException.class, () -> RequestValidator.passportNumberValidate(firstSpacePassportNumber));
        assertAll(() -> RequestValidator.passportNumberValidate(correctPassportNumber));
    }

    @Test
    void passwordValidate() {
        String correctPassword = "Pa$$w0rd";
        String passwordWithoutRequiredCharacters = "PasswOrd";
        String passwordWrongLength = " 123M3543";

        assertEquals(correctPassword, RequestValidator.passwordValidate(correctPassword));

        assertThrows(ValidationException.class, () -> RequestValidator.passwordValidate(passwordWithoutRequiredCharacters));
        assertThrows(ValidationException.class, () -> RequestValidator.passwordValidate(passwordWrongLength));
    }

    @Test
    void emailValidate_whenCorrect_thenReturnEmail() {
        assertAll(
                () -> assertThrows(ValidationException.class, () -> RequestValidator.emailValidate(WITHOUT_DOMAIN_EMAIL)),
                () -> assertThrows(ValidationException.class, () -> RequestValidator.emailValidate(WITHOUT_AT_EMAIL)),
                () -> assertThrows(ValidationException.class, () -> RequestValidator.emailValidate(INCORRECT_SHORT_EMAIL)),
                () -> assertThrows(ValidationException.class, () -> RequestValidator.emailValidate(INCORRECT_SYMBOL_EMAIL)),
                () -> assertEquals(CORRECT_EMAIL, RequestValidator.emailValidate(CORRECT_EMAIL))
        );
    }

    @Test
    void firstNameValidateTest() {
        String FirstNameWrongLength = "A";
        String FirstNameWrongCharacters = "Martin25";
        String correctFirstName1 = "John";
        String correctFirstName2 = "Игнат";

        assertThrows(ValidationException.class, () -> RequestValidator.firstNameValidate(FirstNameWrongLength));
        assertThrows(ValidationException.class, () -> RequestValidator.firstNameValidate(FirstNameWrongCharacters));

        assertAll(() -> RequestValidator.firstNameValidate(correctFirstName1));
        assertAll(() -> RequestValidator.firstNameValidate(correctFirstName2));
    }

    @Test
    void countryOfResidenceValidateTest() {
        String notAllowedCountry1 = "JPN";
        String notAllowedCountry2 = "LTU";
        String notCountry = "abc123";
        String allowedCountry1 = "RUS";
        String allowedCountry2 = "BLR";

        assertThrows(ValidationException.class, () -> RequestValidator.countryOfResidenceValidate(notAllowedCountry1));
        assertThrows(ValidationException.class, () -> RequestValidator.countryOfResidenceValidate(notAllowedCountry2));
        assertThrows(ValidationException.class, () -> RequestValidator.countryOfResidenceValidate(notCountry));

        assertAll(() -> RequestValidator.countryOfResidenceValidate(allowedCountry1));
        assertAll(() -> RequestValidator.countryOfResidenceValidate(allowedCountry2));
    }

    @Test
    void placeOfIssueValidateTest() {
        String incorrectPlaceOfIssue  = "abc123";
        String correctPlaceOfIssue1 = "United States";
        String correctPlaceOfIssue2 = "Russian Federation";

        assertThrows(ValidationException.class, () -> RequestValidator.placeOfIssueValidate(incorrectPlaceOfIssue));

        assertAll(() -> RequestValidator.placeOfIssueValidate(correctPlaceOfIssue1));
        assertAll(() -> RequestValidator.placeOfIssueValidate(correctPlaceOfIssue2));
    }

    @Test
    void securityQuestionValidateTest() {
        String securityQuestionStartsWithSymbol  = "?how old?";
        String securityQuestionWrongLength  = "I?";
        String securityQuestionNotAllowedSymbols  = "what* is<>?";
        String correctSecurityQuestion1 = "Какое ваше любимое блюдо?";
        String correctSecurityQuestion2 = "What is your favorite color?";

        assertThrows(ValidationException.class, () -> RequestValidator.securityQuestionValidate(securityQuestionStartsWithSymbol));
        assertThrows(ValidationException.class, () -> RequestValidator.securityQuestionValidate(securityQuestionWrongLength));
        assertThrows(ValidationException.class, () -> RequestValidator.securityQuestionValidate(securityQuestionNotAllowedSymbols));

        assertAll(() -> RequestValidator.securityQuestionValidate(correctSecurityQuestion1));
        assertAll(() -> RequestValidator.securityQuestionValidate(correctSecurityQuestion2));
    }

    @Test
    public void authorizationTypeValidateTest() {
        AuthorizationTypeIncomingDto falseDto1 = new AuthorizationTypeIncomingDto(true, true, true);
        AuthorizationTypeIncomingDto falseDto2 = new AuthorizationTypeIncomingDto(true, false, true);
        AuthorizationTypeIncomingDto falseDto3 = new AuthorizationTypeIncomingDto(false, false, false);
        AuthorizationTypeIncomingDto correctDto = new AuthorizationTypeIncomingDto(true, false, false);

        assertAll(
                () -> assertFalse(authorizationTypeValidate(falseDto1)),
                () -> assertFalse(authorizationTypeValidate(falseDto2)),
                () -> assertFalse(authorizationTypeValidate(falseDto3)),
                () -> assertTrue(authorizationTypeValidate(correctDto)));
    }
}