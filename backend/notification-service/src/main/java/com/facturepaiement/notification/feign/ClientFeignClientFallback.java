package com.facturepaiement.notification.feign;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClientFeignClientFallback implements ClientFeignClient {

    @Override
    public Map<String, Object> getClientById(Long id) {
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("error", "Service client-service temporairement indisponible");
        fallbackResponse.put("message", "Circuit breaker ouvert - Fallback activé");
        fallbackResponse.put("clientId", id);
        fallbackResponse.put("email", ""); // Email vide en fallback
        return fallbackResponse;
    }
}

