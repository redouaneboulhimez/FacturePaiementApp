package com.facturepaiement.facture.service;

import com.facturepaiement.facture.model.Facture;
import com.facturepaiement.facture.model.StatutFacture;
import com.facturepaiement.facture.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    public Optional<Facture> getFactureById(Long id) {
        return factureRepository.findById(id);
    }

    public List<Facture> getFacturesByClient(Long clientId) {
        return factureRepository.findByClientId(clientId);
    }

    public Facture createFacture(Facture facture) {
        return factureRepository.save(facture);
    }

    public Optional<Facture> updateStatut(Long id, StatutFacture statut) {
        return factureRepository.findById(id)
                .map(facture -> {
                    facture.setStatut(statut);
                    return factureRepository.save(facture);
                });
    }

    public List<Facture> getFacturesByStatut(StatutFacture statut) {
        return factureRepository.findByStatut(statut);
    }

    public List<Facture> getFacturesImpayeesEnRetard() {
        return factureRepository.findByStatutAndDateEcheanceBefore(StatutFacture.EN_ATTENTE, java.time.LocalDate.now());
    }
}

