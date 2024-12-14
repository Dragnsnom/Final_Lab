package com.example.userservice.web.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationInfoDTO {

    @NonNull
    private String mobilePhone;

    @NonNull
    private String clientStatus;

    private String clientId;
}
