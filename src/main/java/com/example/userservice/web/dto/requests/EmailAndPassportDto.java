package com.example.userservice.web.dto.requests;

import com.example.userservice.web.util.annotation.Email;
import com.example.userservice.web.util.annotation.Passport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailAndPassportDto {

    @Email
    @Size(min = 1, max = 50)
    @NotBlank
    private String email;

    @Passport
    @Size(min = 6, max = 10)
    @NotBlank
    private String passportNumber;
}
