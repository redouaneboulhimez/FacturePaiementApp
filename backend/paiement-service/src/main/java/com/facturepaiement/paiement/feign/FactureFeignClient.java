package com.facturepaiement.paiement.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "facture-service", fallback = FactureFeignClientFallback.class)
public interface FactureFeignClient {

    @GetMapping("/factures/{id}")
    Map<String, Object> getFactureById(@PathVariable Long id);

    @PutMapping("/factures/{id}/status")
    Map<String, Object> updateStatut(@PathVariable Long id, @RequestParam String statut);
}

