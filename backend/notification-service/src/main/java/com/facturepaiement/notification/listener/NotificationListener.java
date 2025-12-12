package com.facturepaiement.notification.listener;

import com.facturepaiement.notification.feign.ClientFeignClient;
import com.facturepaiement.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationListener {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ClientFeignClient clientFeignClient;

    @KafkaListener(topics = "paiement-topic", groupId = "notification-group")
    public void handlePaiementNotification(Map<String, Object> message) {
        Long paiementId = Long.valueOf(message.get("paiementId").toString());
        Long factureId = Long.valueOf(message.get("factureId").toString());
        String clientEmail = message.get("clientEmail").toString();
        Double montant = Double.valueOf(message.get("montant").toString());

        notificationService.envoyerNotificationPaiement(paiementId, factureId, clientEmail, montant);
    }

    @KafkaListener(topics = "relance-topic", groupId = "notification-group")
    public void handleRelanceNotification(Map<String, Object> message) {
        Long factureId = Long.valueOf(message.get("factureId").toString());
        Long clientId = Long.valueOf(message.get("clientId").toString());
        Double montant = Double.valueOf(message.get("montant").toString());

        // Récupérer l'email du client via Feign
        String clientEmail = "";
        try {
            Map<String, Object> client = clientFeignClient.getClientById(clientId);
            if (client != null && client.get("email") != null) {
                clientEmail = client.get("email").toString();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du client: " + e.getMessage());
        }

        notificationService.envoyerNotificationRelance(factureId, clientId, clientEmail, montant);
    }
}

