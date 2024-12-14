package com.example.userservice.web.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobilePhoneDTO {

    @NonNull
    String mobilePhone;
}
