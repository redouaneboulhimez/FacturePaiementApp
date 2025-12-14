package com.facturepaiement.notification.controller;

import com.facturepaiement.notification.model.Notification;
import com.facturepaiement.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/client/{email}")
    public ResponseEntity<List<Notification>> getNotificationsByClient(@PathVariable String email) {
        // Décoder l'email si nécessaire (les @ et autres caractères spéciaux sont encodés dans l'URL)
        try {
            String decodedEmail = java.net.URLDecoder.decode(email, "UTF-8");
            return ResponseEntity.ok(notificationService.getNotificationsByClient(decodedEmail));
        } catch (Exception e) {
            // Si le décodage échoue, utiliser l'email tel quel
            return ResponseEntity.ok(notificationService.getNotificationsByClient(email));
        }
    }

    // Endpoint de secours pour éviter les erreurs 404 (redirection vers l'endpoint correct)
    @GetMapping("/clients")
    public ResponseEntity<List<Notification>> getAllNotificationsForClients() {
        // Retourner toutes les notifications si aucun email n'est spécifié
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
}

