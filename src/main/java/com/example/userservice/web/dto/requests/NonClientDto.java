package com.example.userservice.web.dto.requests;

import com.example.userservice.web.util.annotation.Password;
import com.example.userservice.web.util.annotation.PlaceOfIssue;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class NonClientDto {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String countryOfResidence;
    @NotNull
    private String mobilePhone;
    @NotNull
    private String email;
    @NotNull
    private String passportNumber;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate issuanceDate;
    @NotNull
    @PlaceOfIssue
    private String placeOfIssue;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @NotNull
    @Password
    private String password;
    @NotNull
    private String securityQuestion;
    @NotNull
    private String securityAnswer;
}
