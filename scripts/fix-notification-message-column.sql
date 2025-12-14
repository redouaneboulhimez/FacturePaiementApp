-- Script SQL pour corriger la taille de la colonne 'message' dans la table notifications
-- Usage: mysql -u root -p notification_db < scripts/fix-notification-message-column.sql

USE notification_db;

-- Modifier la colonne message pour accepter jusqu'à 1000 caractères
ALTER TABLE notifications MODIFY COLUMN message VARCHAR(1000);

-- Vérifier la structure de la table
DESCRIBE notifications;

