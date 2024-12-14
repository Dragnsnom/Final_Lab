package com.example.userservice.web.util;


import com.example.userservice.web.dto.requests.ClientDto;
import com.example.userservice.web.dto.requests.NonClientDto;
import com.example.userservice.web.dto.requests.AuthorizationTypeIncomingDto;
import jakarta.validation.ValidationException;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class RequestValidator {

    private static final String MOBILE_PHONE_REGEX = "\\d+";

    private static final String PASSPORT_NUMBER_REGEX =
            "([A-Z0-9]*\\d+[A-Z0-9]*(\s|-)?[A-Z0-9]+)|[A-Z]+(\s|-)?[A-Z0-9]*\\d+[A-Z0-9]*";

    private static final String EMAIL_REGEX = "^(?=.{5,30}@)[A-Za-z0-9_]+([A-Za-z0-9_-]+)*(\\.[A-Za-z0-9_-]+)*" +
            "@[^-]([A-Za-z0-9_-]+)(\\.[A-Za-z]+)*(\\.[A-Za-z]{2,3})$";

    private static final String EMAIL_AFTERAT_REGEX = "^[a-zA-Z]+(\\.[a-zA-Z]+)*$";

    private static final String PASSWORD_ALLOWED_SYMBOLS = "[a-zA-Z0-9]+[!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~a-zA-Z0-9]*";

    private static final String SECURITY_QUESTION_OR_ANSWER_REGEX =
            "[a-zA-Z0-9а-яА-Я]+[a-zA-Z0-9а-яА-Я!?,-._\s]*";

    private static final List<String> ALLOWED_COUNTRY_OF_RESIDENCE = List.of("RUS", "BLR", "POL",
            "UKR", "USA", "FR", "ESP", "ITA", "DEU", "CHN");

    private static final String FIRSTNAME_OR_LASTNAME_REGEX = "[a-zA-Z]+||[а-яА-Я]+";

    private static final String PLACE_OF_ISSUE_REGEX = "[a-zA-Z]+\s+[a-zA-Z]+||[а-яА-Я]+\s+[а-яА-Я]+";

    private static final List<String> PASSWORD_SYMBOLS_GROUP = List.of(
            ".*[a-z].*", ".*[A-Z].*", ".*[0-9].*", ".*[!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~].*");

    public String mobilePhoneValidate(String mobilePhone) {
        String newMobilePhone = mobilePhone.replace(" ", "");

        if (!newMobilePhone.matches(MOBILE_PHONE_REGEX)) {
            throw new ValidationException("A phone number can only contain numbers");
        }

        if (!newMobilePhone.startsWith("7") && !newMobilePhone.startsWith("8") || newMobilePhone.length() != 11) {
            throw new ValidationException("The length of the phone number must be 11 characters");
        }

        if (newMobilePhone.startsWith("8")) {
            newMobilePhone = new StringBuilder(newMobilePhone).replace(0, 1, "7").toString();
        }

        return newMobilePhone;
    }

    public String passportNumberValidate(String passportNumber) {
        if (!passportNumber.matches(PASSPORT_NUMBER_REGEX)) {
            throw new ValidationException("Incorrect passport number: " + passportNumber);
        }

        passportNumber = passportNumber.replace(" ", "");
        passportNumber = passportNumber.replace("-", "");

        if (passportNumber.length() < 6 || passportNumber.length() > 10) {
            throw new ValidationException("Incorrect passport number: " + passportNumber);
        }
        return passportNumber;
    }

    public String passwordValidate(String password) {
        if (password.length() < 6 || password.length() > 20
                || !password.matches(PASSWORD_ALLOWED_SYMBOLS)) {
            throw new ValidationException("The new password must be no shorter than 6 characters and " +
                    "no longer than 20 characters");
        }
        int numberOfContainedSymbolGroups = 0;
        for (String symbolGroup: PASSWORD_SYMBOLS_GROUP) {
            if(password.matches(symbolGroup)){
                numberOfContainedSymbolGroups++;
            }
        }

        if (numberOfContainedSymbolGroups < 3) {
            throw new ValidationException("The new password must contain at least 3 symbol groups");
        }
        return password;
    }

    public String verificationCodeValidate(String verificationCode) {
        String newVerificationCode = verificationCode.replace(" ", "");

        String regex = "\\d{6}";

        if (!newVerificationCode.matches(regex)) {
            throw new ValidationException("A verification code must consist of 6 digits: " + newVerificationCode);
        }

        return newVerificationCode;
    }

    public String emailValidate(String email) throws ValidationException {
        if (!email.matches(EMAIL_REGEX)){
            throw new ValidationException("Incorrect email: " + email);
        }
        String [] mas = email.split("@");
        String afterAt = mas[1];
        String beforeAt = mas[0];

        if (beforeAt.equalsIgnoreCase("admin")
                || (afterAt.length() > 19 || afterAt.length() < 5)
                || !afterAt.matches(EMAIL_AFTERAT_REGEX)) {
            throw new ValidationException("Incorrect email: " + email);
        }
        return email;
    }

    public String firstNameValidate(String firstName) {
        firstName = firstName.replace(" ", "");
        firstName = firstName.replace("-", "");

        if (firstName.length() < 2 || firstName.length() > 30
                || !firstName.matches(FIRSTNAME_OR_LASTNAME_REGEX)) {
            throw new ValidationException("Incorrect first name");
        }
        return firstName;
    }

    public String lastNameValidate(String lastName) {
        lastName = lastName.replace(" ", "");
        lastName = lastName.replace("-", "");

        if (lastName.length() < 2 || lastName.length() > 30
                || !lastName.matches(FIRSTNAME_OR_LASTNAME_REGEX)) {
            throw new ValidationException("Incorrect last name");
        }
        return lastName;
    }

    public void countryOfResidenceValidate(String countryOfResidence) {
        if (!ALLOWED_COUNTRY_OF_RESIDENCE.contains(countryOfResidence)) {
            throw new ValidationException("IllegalArgument country");
        }
    }

    public void placeOfIssueValidate(String placeOfIssue) {
        if (placeOfIssue.length() < 5 || placeOfIssue.length() > 30
                || !placeOfIssue.matches(PLACE_OF_ISSUE_REGEX)) {
            throw new ValidationException("Incorrect place of issue");
        }
    }

    public void securityQuestionValidate(String securityQuestion) {
        if (securityQuestion.length() < 3 || securityQuestion.length() > 50
                || !securityQuestion.matches(SECURITY_QUESTION_OR_ANSWER_REGEX)) {
            throw new ValidationException("Incorrect security question");
        }
    }

    public void securityAnswerValidate(String securityAnswer) {
        if (securityAnswer.length() < 3 || securityAnswer.length() > 50
                || !securityAnswer.matches(SECURITY_QUESTION_OR_ANSWER_REGEX)) {
            throw new ValidationException("Incorrect security answer");
        }
    }

    public void nonClientDtoValidate(NonClientDto nonClientDto) {
        String firstNameWithoutSpaces = firstNameValidate(nonClientDto.getFirstName());
        nonClientDto.setFirstName(firstNameWithoutSpaces);
        String lastNameWithoutSpaces = lastNameValidate(nonClientDto.getLastName());
        nonClientDto.setLastName(lastNameWithoutSpaces);
        String mobilePhoneWithoutSpaces = mobilePhoneValidate(nonClientDto.getMobilePhone());
        nonClientDto.setMobilePhone(mobilePhoneWithoutSpaces);
        String passportNumberWithoutSpaces = passportNumberValidate(nonClientDto.getPassportNumber());
        nonClientDto.setPassportNumber(passportNumberWithoutSpaces);

        countryOfResidenceValidate(nonClientDto.getCountryOfResidence());
        emailValidate(nonClientDto.getEmail());
        securityQuestionValidate(nonClientDto.getSecurityQuestion());
        securityAnswerValidate(nonClientDto.getSecurityAnswer());
    }

    public void clientDtoValidate(ClientDto clientDto) {
        String mobilePhoneWithoutSpaces = mobilePhoneValidate(clientDto.getMobilePhone());
        clientDto.setMobilePhone(mobilePhoneWithoutSpaces);

        emailValidate(clientDto.getEmail());
        securityQuestionValidate(clientDto.getSecurityQuestion());
        securityAnswerValidate(clientDto.getSecurityAnswer());
    }

    public static boolean authorizationTypeValidate(AuthorizationTypeIncomingDto dto) {
        int count = 0;
        if (dto.isLogpass()) {
            count++;
        }
        if (dto.isPincode()) {
            count++;
        }
        if (dto.isBiometrics()) {
            count++;
        }

        return count == 1 ? true : false;
    }

    public String fingerprintValidate(String fingerprint) throws IllegalArgumentException {
        String validFingerprint = fingerprint.replace(" ", "");

        String regex = "\\d{6}";

        if (!validFingerprint.matches(regex)) {
            throw new ValidationException("A fingerprint must consist of 6 digits: " + validFingerprint);
        }

        return validFingerprint;
    }
}
