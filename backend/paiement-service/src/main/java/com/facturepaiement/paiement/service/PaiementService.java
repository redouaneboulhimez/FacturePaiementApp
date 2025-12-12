package com.facturepaiement.paiement.service;

import com.facturepaiement.paiement.feign.ClientFeignClient;
import com.facturepaiement.paiement.feign.FactureFeignClient;
import com.facturepaiement.paiement.model.Paiement;
import com.facturepaiement.paiement.repository.PaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        BigDecimal montantFacture = new BigDecimal(factureMap.get("montant").toString());
        
        // Vérifier le montant
        if (montant.compareTo(montantFacture) != 0) {
            throw new RuntimeException("Le montant payé ne correspond pas au montant de la facture");
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
        Long clientId = Long.valueOf(factureMap.get("clientId").toString());
        Map<String, Object> clientMap = clientFeignClient.getClientById(clientId);
        
        String clientEmail = "";
        if (clientMap != null && !clientMap.containsKey("error") && clientMap.get("email") != null) {
            clientEmail = clientMap.get("email").toString();
        }

        // Envoyer un message Kafka pour notification
        Map<String, Object> notificationMessage = new HashMap<>();
        notificationMessage.put("paiementId", paiement.getId());
        notificationMessage.put("factureId", factureId);
        notificationMessage.put("clientEmail", clientEmail);
        notificationMessage.put("montant", montant.doubleValue());

        kafkaTemplate.send("paiement-topic", notificationMessage);

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

