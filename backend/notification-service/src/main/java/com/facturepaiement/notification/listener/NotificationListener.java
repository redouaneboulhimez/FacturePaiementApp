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
        try {
            System.out.println("📨 Message Kafka reçu (paiement-topic): " + message);
            
            if (message == null || message.isEmpty()) {
                System.err.println("❌ Message Kafka vide ou null");
                return;
            }
            
            // Vérifier et extraire paiementId (peut être null)
            Long paiementId = null;
            if (message.get("paiementId") != null) {
                paiementId = Long.valueOf(message.get("paiementId").toString());
            }
            
            // Vérifier factureId (obligatoire)
            if (message.get("factureId") == null) {
                System.err.println("❌ factureId manquant dans le message Kafka");
                return;
            }
            Long factureId = Long.valueOf(message.get("factureId").toString());
            
            // Vérifier montant (obligatoire)
            if (message.get("montant") == null) {
                System.err.println("❌ montant manquant dans le message Kafka");
                return;
            }
            Double montant = Double.valueOf(message.get("montant").toString());
            
            // clientEmail peut être vide mais on continue quand même
            String clientEmail = message.get("clientEmail") != null 
                ? message.get("clientEmail").toString() 
                : "";

            System.out.println("✅ Données extraites - PaiementId: " + paiementId + ", FactureId: " + factureId + ", Email: " + clientEmail + ", Montant: " + montant);
            
            notificationService.envoyerNotificationPaiement(paiementId, factureId, clientEmail, montant);
            System.out.println("✅ Notification créée avec succès pour le paiement #" + paiementId);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du traitement du message Kafka (paiement-topic): " + e.getMessage());
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "relance-topic", groupId = "notification-group")
    public void handleRelanceNotification(Map<String, Object> message) {
        try {
            System.out.println("📨 Message Kafka reçu (relance-topic): " + message);
            
            if (message == null || message.isEmpty()) {
                System.err.println("❌ Message Kafka vide ou null");
                return;
            }
            
            // Vérifier factureId (obligatoire)
            if (message.get("factureId") == null) {
                System.err.println("❌ factureId manquant dans le message Kafka");
                return;
            }
            Long factureId = Long.valueOf(message.get("factureId").toString());
            
            // Vérifier clientId (obligatoire)
            if (message.get("clientId") == null) {
                System.err.println("❌ clientId manquant dans le message Kafka");
                return;
            }
            Long clientId = Long.valueOf(message.get("clientId").toString());
            
            // Vérifier montant (obligatoire)
            if (message.get("montant") == null) {
                System.err.println("❌ montant manquant dans le message Kafka");
                return;
            }
            Double montant = Double.valueOf(message.get("montant").toString());

            // Récupérer l'email du client via Feign
            String clientEmail = "";
            try {
                Map<String, Object> client = clientFeignClient.getClientById(clientId);
                if (client != null && !client.containsKey("error") && client.get("email") != null) {
                    clientEmail = client.get("email").toString();
                    System.out.println("✅ Email client récupéré: " + clientEmail);
                } else {
                    System.out.println("⚠️ Email client non disponible (fallback activé ou client introuvable)");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de la récupération du client: " + e.getMessage());
                // Continue quand même avec un email vide
            }

            System.out.println("✅ Données extraites - FactureId: " + factureId + ", ClientId: " + clientId + ", Email: " + clientEmail + ", Montant: " + montant);
            
            notificationService.envoyerNotificationRelance(factureId, clientId, clientEmail, montant);
            System.out.println("✅ Notification de relance créée avec succès pour la facture #" + factureId);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du traitement du message Kafka (relance-topic): " + e.getMessage());
            e.printStackTrace();
        }
    }
}

