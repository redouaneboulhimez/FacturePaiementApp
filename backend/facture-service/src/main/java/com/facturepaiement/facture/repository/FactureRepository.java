package com.facturepaiement.facture.repository;

import com.facturepaiement.facture.model.Facture;
import com.facturepaiement.facture.model.StatutFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByClientId(Long clientId);
    List<Facture> findByStatut(StatutFacture statut);
    List<Facture> findByStatutAndDateEcheanceBefore(StatutFacture statut, LocalDate date);
    Optional<Facture> findById(Long id);
}

