package com.facturepaiement.paiement.service;

import com.facturepaiement.paiement.feign.ClientFeignClient;
import com.facturepaiement.paiement.feign.FactureFeignClient;
import com.facturepaiement.paiement.model.Paiement;
import com.facturepaiement.paiement.repository.PaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private FactureFeignClient factureFeignClient;

    @Autowired
    private ClientFeignClient clientFeignClient;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public Paiement effectuerPaiement(Long factureId, BigDecimal montant, String methodePaiement) {
        // Récupérer la facture via Feign (avec Circuit Breaker et Fallback)
        Map<String, Object> factureMap = factureFeignClient.getFactureById(factureId);
        
        // Vérifier si c'est une réponse de fallback
        if (factureMap == null || factureMap.containsKey("error")) {
            String errorMsg = factureMap != null && factureMap.containsKey("message") 
                ? factureMap.get("message").toString() 
                : "Service facture-service indisponible";
            throw new RuntimeException(errorMsg);
        }

        // Vérifier que la facture n'est pas déjà payée
        Object statutObj = factureMap.get("statut");
        if (statutObj != null && "PAYEE".equalsIgnoreCase(statutObj.toString())) {
            throw new RuntimeException("Cette facture a déjà été payée");
        }

        // Vérifier que le montant est présent dans la facture
        Object montantObj = factureMap.get("montant");
        if (montantObj == null) {
            throw new RuntimeException("Montant de la facture non disponible");
        }
        BigDecimal montantFacture = new BigDecimal(montantObj.toString());
        
        // Normaliser et vérifier le montant (pour éviter les problèmes de précision)
        BigDecimal montantNormalized = montant.setScale(2, RoundingMode.HALF_UP);
        BigDecimal montantFactureNormalized = montantFacture.setScale(2, RoundingMode.HALF_UP);
        
        if (montantNormalized.compareTo(montantFactureNormalized) != 0) {
            throw new RuntimeException("Le montant payé (" + montantNormalized + 
                " €) ne correspond pas au montant de la facture (" + montantFactureNormalized + " €)");
        }

        // Créer le paiement
        Paiement paiement = new Paiement(factureId, montant, methodePaiement);
        paiement = paiementRepository.save(paiement);

        // Mettre à jour le statut de la facture via Feign (avec Circuit Breaker)
        Map<String, Object> updateResult = factureFeignClient.updateStatut(factureId, "PAYEE");
        if (updateResult != null && updateResult.containsKey("error")) {
            // Log l'erreur mais continue le processus
            System.err.println("Erreur lors de la mise à jour du statut: " + updateResult.get("message"));
        }

        // Récupérer les informations du client (avec Circuit Breaker et Fallback)
        Object clientIdObj = factureMap.get("clientId");
        if (clientIdObj == null) {
            throw new RuntimeException("Client ID non disponible dans la facture");
        }
        Long clientId = Long.valueOf(clientIdObj.toString());
        Map<String, Object> clientMap = clientFeignClient.getClientById(clientId);
        
        String clientEmail = "";
        if (clientMap != null && !clientMap.containsKey("error") && clientMap.get("email") != null) {
            clientEmail = clientMap.get("email").toString();
        }

        // Envoyer un message Kafka pour notification (avec gestion d'erreur)
        try {
            Map<String, Object> notificationMessage = new HashMap<>();
            notificationMessage.put("paiementId", paiement.getId());
            notificationMessage.put("factureId", factureId);
            notificationMessage.put("clientEmail", clientEmail);
            notificationMessage.put("montant", montant.doubleValue());

            kafkaTemplate.send("paiement-topic", notificationMessage);
        } catch (Exception e) {
            // Log l'erreur mais ne fait pas échouer le paiement si Kafka est indisponible
            System.err.println("Erreur lors de l'envoi Kafka (paiement enregistré): " + e.getMessage());
        }

        return paiement;
    }

    public Optional<Paiement> getPaiementById(Long id) {
        return paiementRepository.findById(id);
    }

    public List<Paiement> getPaiementsByFacture(Long factureId) {
        return paiementRepository.findByFactureId(factureId);
    }

    public List<Paiement> getAllPaiements() {
        return paiementRepository.findAll();
    }
}

