package com.facturepaiement.paiement.controller;

import com.facturepaiement.paiement.model.Paiement;
import com.facturepaiement.paiement.service.PaiementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paiements")
@CrossOrigin(origins = "*")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    @PostMapping
    public ResponseEntity<?> effectuerPaiement(@RequestBody Map<String, Object> paiementRequest) {
        try {
            Long factureId = Long.valueOf(paiementRequest.get("factureId").toString());
            BigDecimal montant = new BigDecimal(paiementRequest.get("montant").toString());
            String methodePaiement = paiementRequest.get("methodePaiement") != null 
                    ? paiementRequest.get("methodePaiement").toString() 
                    : "VIREMENT";

            Paiement paiement = paiementService.effectuerPaiement(factureId, montant, methodePaiement);
            return ResponseEntity.status(HttpStatus.CREATED).body(paiement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paiement> getPaiementById(@PathVariable Long id) {
        return paiementService.getPaiementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/facture/{factureId}")
    public ResponseEntity<List<Paiement>> getPaiementsByFacture(@PathVariable Long factureId) {
        return ResponseEntity.ok(paiementService.getPaiementsByFacture(factureId));
    }

    @GetMapping
    public ResponseEntity<List<Paiement>> getAllPaiements() {
        return ResponseEntity.ok(paiementService.getAllPaiements());
    }
}

