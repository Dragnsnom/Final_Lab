package com.example.userservice.app.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "credit-service", url = "http://localhost:8083/credit/api/v1")
public interface CreditServiceClient {

    @GetMapping("/credits")
    ResponseEntity checkClientCredits(@RequestParam("clientId") UUID clientId);
}
