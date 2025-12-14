package com.facturepaiement.notification.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)  // Peut être null pour les notifications de relance
    private Long paiementId;

    @Column(nullable = false)
    private Long factureId;

    @Column(nullable = false)
    private String clientEmail;

    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private String type; // PAIEMENT, RELANCE

    @Column(nullable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    @Column(nullable = false)
    private String statut = "ENVOYE"; // ENVOYE, ECHEC

    @Column(length = 1000) // Augmenter la taille pour permettre des messages plus longs
    private String message;

    public Notification() {}

    public Notification(Long paiementId, Long factureId, String clientEmail, Double montant, String type) {
        this.paiementId = paiementId;
        this.factureId = factureId;
        this.clientEmail = clientEmail;
        this.montant = montant;
        this.type = type;
        this.dateEnvoi = LocalDateTime.now();
        this.statut = "ENVOYE";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaiementId() {
        return paiementId;
    }

    public void setPaiementId(Long paiementId) {
        this.paiementId = paiementId;
    }

    public Long getFactureId() {
        return factureId;
    }

    public void setFactureId(Long factureId) {
        this.factureId = factureId;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

