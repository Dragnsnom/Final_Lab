package com.example.userservice.web.dto.requests;

import com.example.userservice.web.util.annotation.SecurityQA;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing question and answer change request.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "Security question and answer change input DTO")
public class SecurityQuestionAnswerDto {
    /**
     * The security question for the question and answer change request.
     */
    @NotBlank(message = "Security question cannot be empty")
    @Size(min = 3, max = 50, message = "Security question must be between 3 and 50 characters")
    @SecurityQA(message = "Invalid security question format")
    @Schema(description = "Security question", example = "What is your favorite color?", type = "string", format = "string")
    private String securityQuestion;

    /**
     * The security answer for the question and answer change request.
     */
    @NotBlank(message = "Security answer cannot be empty")
    @Size(min = 3, max = 50, message = "Security answer must be between 3 and 50 characters")
    @SecurityQA(message = "Invalid security answer format")
    @Schema(description = "Security answer", example = "Black", type = "string", format = "string")
    private String securityAnswer;
}
