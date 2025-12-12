package com.facturepaiement.paiement.feign;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FactureFeignClientFallback implements FactureFeignClient {

    @Override
    public Map<String, Object> getFactureById(Long id) {
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("error", "Service facture-service temporairement indisponible");
        fallbackResponse.put("message", "Circuit breaker ouvert - Fallback activé");
        fallbackResponse.put("factureId", id);
        return fallbackResponse;
    }

    @Override
    public Map<String, Object> updateStatut(Long id, String statut) {
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("error", "Service facture-service temporairement indisponible");
        fallbackResponse.put("message", "Impossible de mettre à jour le statut - Fallback activé");
        fallbackResponse.put("factureId", id);
        fallbackResponse.put("statut", statut);
        return fallbackResponse;
    }
}

