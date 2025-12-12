package com.facturepaiement.notification.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "client-service", fallback = ClientFeignClientFallback.class)
public interface ClientFeignClient {

    @GetMapping("/clients/{id}")
    Map<String, Object> getClientById(@PathVariable Long id);
}

