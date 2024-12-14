package com.example.userservice.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("Verification")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Verification implements Serializable {
    @Id
    private String mobilePhone;
    private String verificationCode;
    private LocalDateTime blockExpiration;
    private Integer verificationAttempts;
    private LocalDateTime codeLifetime;
}
