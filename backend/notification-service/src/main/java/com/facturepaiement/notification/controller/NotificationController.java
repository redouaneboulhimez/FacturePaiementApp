package com.facturepaiement.notification.controller;

import com.facturepaiement.notification.model.Notification;
import com.facturepaiement.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/client/{email}")
    public ResponseEntity<List<Notification>> getNotificationsByClient(@PathVariable String email) {
        return ResponseEntity.ok(notificationService.getNotificationsByClient(email));
    }
}

