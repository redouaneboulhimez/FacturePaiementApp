package com.facturepaiement.facture.controller;

import com.facturepaiement.facture.model.Facture;
import com.facturepaiement.facture.model.StatutFacture;
import com.facturepaiement.facture.service.FactureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/factures")
public class FactureController {

    @Autowired
    private FactureService factureService;

    @GetMapping
    public ResponseEntity<List<Facture>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        return factureService.getFactureById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Facture>> getFacturesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(factureService.getFacturesByClient(clientId));
    }

    @PostMapping
    public ResponseEntity<Facture> createFacture(@Valid @RequestBody Facture facture) {
        Facture created = factureService.createFacture(facture);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Facture> updateStatut(@PathVariable Long id, @RequestParam StatutFacture statut) {
        return factureService.updateStatut(id, statut)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Facture>> getFacturesByStatut(@PathVariable StatutFacture statut) {
        return ResponseEntity.ok(factureService.getFacturesByStatut(statut));
    }
}

