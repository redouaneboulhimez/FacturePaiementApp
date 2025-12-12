package com.facturepaiement.notification.service;

import com.facturepaiement.notification.model.Notification;
import com.facturepaiement.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public Notification envoyerNotificationPaiement(Long paiementId, Long factureId, String clientEmail, Double montant) {
        Notification notification = new Notification(paiementId, factureId, clientEmail, montant, "PAIEMENT");
        
        try {
            // Envoyer email
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(clientEmail);
                message.setSubject("Confirmation de paiement - Facture #" + factureId);
                message.setText("Votre paiement de " + montant + " € pour la facture #" + factureId + " a été effectué avec succès.");
                mailSender.send(message);
            }
            
            // Log dans la console (simulation SMS/notification système)
            System.out.println("NOTIFICATION PAIEMENT envoyée à " + clientEmail + " - Montant: " + montant + " €");
            
            notification.setMessage("Notification de paiement envoyée avec succès");
            notification.setStatut("ENVOYE");
        } catch (Exception e) {
            notification.setMessage("Erreur lors de l'envoi: " + e.getMessage());
            notification.setStatut("ECHEC");
        }
        
        return notificationRepository.save(notification);
    }

    public Notification envoyerNotificationRelance(Long factureId, Long clientId, String clientEmail, Double montant) {
        Notification notification = new Notification(null, factureId, clientEmail, montant, "RELANCE");
        
        try {
            // Envoyer email de relance
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(clientEmail);
                message.setSubject("Rappel - Facture #" + factureId + " en retard");
                message.setText("Votre facture #" + factureId + " d'un montant de " + montant + " € est en retard de paiement. Veuillez régulariser votre situation.");
                mailSender.send(message);
            }
            
            System.out.println("NOTIFICATION RELANCE envoyée à " + clientEmail + " - Facture #" + factureId + " - Montant: " + montant + " €");
            
            notification.setMessage("Notification de relance envoyée avec succès");
            notification.setStatut("ENVOYE");
        } catch (Exception e) {
            notification.setMessage("Erreur lors de l'envoi: " + e.getMessage());
            notification.setStatut("ECHEC");
        }
        
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByClient(String clientEmail) {
        return notificationRepository.findByClientEmail(clientEmail);
    }
}

