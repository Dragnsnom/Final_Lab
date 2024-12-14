package com.example.userservice.web.util.validation;

import com.example.userservice.web.dto.requests.SecurityQuestionAnswerDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.example.userservice.web.util.validation.SecurityQAValidator.SECURITY_QUESTION_ANSWER_PATTERN;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityQATest {
    private static final String VALID_SECURITY_QUESTION_EN = "What is your favorite color?";
    private static final String VALID_SECURITY_QUESTION_RU = "Какой ваш любимый цвет?";
    private static final String INVALID_SECURITY_QUESTION_JP = "あなたの好きな色は何ですか？";
    private static final String INVALID_SECURITY_QUESTION_HB = "מהו הצבע האהוב עליך?";
    private static final String VALID_SECURITY_ANSWER_EN = "Black";
    private static final String VALID_SECURITY_ANSWER_RU = "Черный";
    private static final String INVALID_SECURITY_ANSWER_JP = "私の好きな色は黒です";
    private static final String INVALID_SECURITY_ANSWER_HB = "הצבע האהוב עלי הוא שחור";

    @ParameterizedTest
    @MethodSource("securityQuestionValidDtos")
    void successMatch_WhenDtoIsValid_ReturnTrue(SecurityQuestionAnswerDto securityQuestionAnswerDto) {
        String question = securityQuestionAnswerDto.getSecurityQuestion();
        String answer = securityQuestionAnswerDto.getSecurityAnswer();

        assertTrue(question.matches(SECURITY_QUESTION_ANSWER_PATTERN));
        assertTrue(answer.matches(SECURITY_QUESTION_ANSWER_PATTERN));
    }
    @ParameterizedTest
    @MethodSource("securityQuestionInvalidDtos")
    void errorMatch_WhenDtoIsValid_ReturnFalse(SecurityQuestionAnswerDto securityQuestionAnswerDto) {
        String question = securityQuestionAnswerDto.getSecurityQuestion();
        String answer = securityQuestionAnswerDto.getSecurityAnswer();

        assertFalse(question.matches(SECURITY_QUESTION_ANSWER_PATTERN));
        assertFalse(answer.matches(SECURITY_QUESTION_ANSWER_PATTERN));
    }

    private static Stream<SecurityQuestionAnswerDto> securityQuestionValidDtos() {
        return Stream.of(
                new SecurityQuestionAnswerDto(VALID_SECURITY_QUESTION_EN, VALID_SECURITY_ANSWER_EN),
                new SecurityQuestionAnswerDto(VALID_SECURITY_QUESTION_RU, VALID_SECURITY_ANSWER_RU)
        );
    }
    private static Stream<SecurityQuestionAnswerDto> securityQuestionInvalidDtos() {
        return Stream.of(
                new SecurityQuestionAnswerDto(INVALID_SECURITY_QUESTION_JP, INVALID_SECURITY_ANSWER_JP),
                new SecurityQuestionAnswerDto(INVALID_SECURITY_QUESTION_HB, INVALID_SECURITY_ANSWER_HB)
        );
    }
}
