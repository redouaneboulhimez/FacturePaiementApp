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
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    @PostMapping
    public ResponseEntity<?> effectuerPaiement(@RequestBody Map<String, Object> paiementRequest) {
        try {
            // Validation des champs requis
            if (paiementRequest == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Le corps de la requête ne peut pas être vide"));
            }
            
            if (!paiementRequest.containsKey("factureId") || paiementRequest.get("factureId") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Le champ 'factureId' est requis"));
            }
            
            if (!paiementRequest.containsKey("montant") || paiementRequest.get("montant") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Le champ 'montant' est requis"));
            }

            // Conversion et validation des types
            Long factureId;
            try {
                factureId = Long.valueOf(paiementRequest.get("factureId").toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Le champ 'factureId' doit être un nombre valide"));
            }

            BigDecimal montant;
            try {
                montant = new BigDecimal(paiementRequest.get("montant").toString());
                if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Le montant doit être supérieur à 0"));
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Le champ 'montant' doit être un nombre valide"));
            }

            String methodePaiement = paiementRequest.get("methodePaiement") != null 
                    ? paiementRequest.get("methodePaiement").toString() 
                    : "VIREMENT";

            Paiement paiement = paiementService.effectuerPaiement(factureId, montant, methodePaiement);
            return ResponseEntity.status(HttpStatus.CREATED).body(paiement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du traitement du paiement: " + e.getMessage()));
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

