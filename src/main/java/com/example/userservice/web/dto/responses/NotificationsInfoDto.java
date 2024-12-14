package com.example.userservice.web.dto.responses;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class NotificationsInfoDto {
    @NonNull
    private String email;
    @NonNull
    private Boolean smsNotification;
    @NonNull
    private Boolean pushNotification;
    @NonNull
    private Boolean emailSubscription;
}
