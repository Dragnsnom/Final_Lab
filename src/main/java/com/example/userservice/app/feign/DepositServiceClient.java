package com.example.userservice.app.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "deposit-service", url = "http://localhost:8083/deposit/api/v1")
public interface DepositServiceClient {

    @GetMapping("/deposits")
    ResponseEntity checkClientDeposits(@RequestParam("clientId") UUID clientId);
}
