# 🧪 Guide de Laboratoire - Application Facture Paiement

## Objectif

Ce guide vous accompagne étape par étape pour créer, configurer et tester l'application de gestion de factures et paiements.

## 📚 Étapes du Laboratoire

### Phase 1: Préparation de l'environnement

#### Étape 1.1: Installation des outils

1. **Vérifier Java 17**
```bash
java -version
# Doit afficher version 17 ou supérieure
```

2. **Vérifier Maven**
```bash
mvn -version
# Doit afficher version 3.6 ou supérieure
```

3. **Installer MySQL**
- Télécharger depuis https://dev.mysql.com/downloads/
- Installer et configurer avec utilisateur `root` et mot de passe `root`

4. **Installer Kafka** (Option Docker recommandée)
```bash
# Créer docker-compose.yml pour Kafka
docker-compose up -d
```

#### Étape 1.2: Configuration MySQL

```sql
-- Se connecter à MySQL
mysql -u root -p

-- Créer les bases de données
CREATE DATABASE client_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE facture_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE paiement_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Vérifier
SHOW DATABASES;
```

### Phase 2: Construction du projet

#### Étape 2.1: Build du projet parent

```bash
# À la racine du projet
mvn clean install -DskipTests
```

#### Étape 2.2: Vérification de la structure

Vérifier que tous les modules sont présents:
- eureka-server
- config-server
- api-gateway
- client-service
- facture-service
- paiement-service
- notification-service

### Phase 3: Démarrage des services (Ordre important!)

#### Étape 3.1: Démarrer Eureka Server

```bash
cd backend/eureka-server
mvn spring-boot:run
```

**Vérification**: Ouvrir http://localhost:8761
- Vous devriez voir la page d'accueil d'Eureka
- Aucun service enregistré pour le moment

#### Étape 3.2: Démarrer Config Server

```bash
# Dans un nouveau terminal
cd backend/config-server
mvn spring-boot:run
```

**Vérification**: 
- Vérifier les logs: "Started ConfigServerApplication"
- Vérifier dans Eureka: le service `config-server` doit apparaître

#### Étape 3.3: Démarrer Client Service

```bash
# Dans un nouveau terminal
cd backend/client-service
mvn spring-boot:run
```

**Vérification**:
- Vérifier les logs: "Started ClientServiceApplication"
- Vérifier dans Eureka: le service `client-service` doit apparaître
- Vérifier la base de données: la table `clients` doit être créée

#### Étape 3.4: Démarrer Facture Service

```bash
# Dans un nouveau terminal
cd backend/facture-service
mvn spring-boot:run
```

**Vérification**:
- Vérifier les logs: "Started FactureServiceApplication"
- Vérifier dans Eureka: le service `facture-service` doit apparaître
- Vérifier la base de données: la table `factures` doit être créée

#### Étape 3.5: Démarrer Paiement Service

```bash
# Dans un nouveau terminal
cd backend/paiement-service
mvn spring-boot:run
```

**Vérification**:
- Vérifier les logs: "Started PaiementServiceApplication"
- Vérifier dans Eureka: le service `paiement-service` doit apparaître

#### Étape 3.6: Démarrer Notification Service

```bash
# Dans un nouveau terminal
cd backend/notification-service
mvn spring-boot:run
```

**Vérification**:
- Vérifier les logs: "Started NotificationServiceApplication"
- Vérifier dans Eureka: le service `notification-service` doit apparaître

#### Étape 3.7: Démarrer API Gateway

```bash
# Dans un nouveau terminal
cd backend/api-gateway
mvn spring-boot:run
```

**Vérification**:
- Vérifier les logs: "Started GatewayApplication"
- Vérifier dans Eureka: le service `api-gateway` doit apparaître

### Phase 4: Tests fonctionnels

#### Étape 4.1: Test CRUD Client

**Créer un client**
```bash
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Jean Dupont",
    "email": "jean.dupont@example.com",
    "telephone": "0123456789",
    "adresse": "123 Rue Example, Paris"
  }'
```

**Résultat attendu**: JSON avec l'ID du client créé

**Lister les clients**
```bash
curl http://localhost:8080/api/clients
```

**Obtenir un client**
```bash
curl http://localhost:8080/api/clients/1
```

#### Étape 4.2: Test CRUD Facture

**Créer une facture**
```bash
curl -X POST http://localhost:8080/api/factures \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "montant": 1500.00,
    "dateEmission": "2024-01-15",
    "dateEcheance": "2024-02-15",
    "description": "Facture de services informatiques"
  }'
```

**Lister les factures**
```bash
curl http://localhost:8080/api/factures
```

**Obtenir les factures d'un client**
```bash
curl http://localhost:8080/api/factures/client/1
```

#### Étape 4.3: Test Paiement et Notification

**Effectuer un paiement**
```bash
curl -X POST http://localhost:8080/api/paiements \
  -H "Content-Type: application/json" \
  -d '{
    "factureId": 1,
    "montant": 1500.00,
    "methodePaiement": "VIREMENT"
  }'
```

**Vérifications**:
1. Le paiement est créé
2. Le statut de la facture passe à `PAYEE`
3. Un message Kafka est envoyé
4. Une notification est créée dans `notification_db`

**Vérifier le statut de la facture**
```bash
curl http://localhost:8080/api/factures/1
# Le statut doit être "PAYEE"
```

**Vérifier les notifications**
```bash
curl http://localhost:8080/api/notifications
```

#### Étape 4.4: Test du Batch de Relance

**Créer une facture impayée avec date d'échéance passée**
```bash
curl -X POST http://localhost:8080/api/factures \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "montant": 2000.00,
    "dateEmission": "2024-01-01",
    "dateEcheance": "2024-01-10",
    "description": "Facture en retard"
  }'
```

**Déclencher le batch manuellement** (ou attendre minuit)
- Le batch s'exécute automatiquement tous les jours à minuit
- Pour tester immédiatement, vous pouvez modifier le cron dans `BatchScheduler.java` temporairement

**Vérifications**:
1. Le statut de la facture passe à `EN_RETARD`
2. Un message Kafka de relance est envoyé
3. Une notification de relance est créée

### Phase 5: Tests d'intégration

#### Étape 5.1: Scénario complet

1. Créer 3 clients
2. Créer 5 factures pour différents clients
3. Effectuer des paiements pour certaines factures
4. Vérifier les notifications générées
5. Vérifier les statuts des factures

#### Étape 5.2: Test de résilience

1. Arrêter temporairement le service Facture
2. Essayer d'effectuer un paiement
3. Observer le comportement (circuit breaker si configuré)
4. Redémarrer le service Facture

### Phase 6: Monitoring et Logs

#### Étape 6.1: Vérifier Eureka Dashboard

Ouvrir http://localhost:8761
- Tous les services doivent être enregistrés
- Statut: UP (vert)

#### Étape 6.2: Vérifier les logs

Consulter les logs de chaque service pour:
- Erreurs éventuelles
- Messages Kafka
- Requêtes HTTP

#### Étape 6.3: Vérifier les bases de données

```sql
-- Vérifier les clients
USE client_db;
SELECT * FROM clients;

-- Vérifier les factures
USE facture_db;
SELECT * FROM factures;

-- Vérifier les paiements
USE paiement_db;
SELECT * FROM paiements;

-- Vérifier les notifications
USE notification_db;
SELECT * FROM notifications;
```

## 🐛 Dépannage

### Problème: Service ne démarre pas

**Solutions**:
1. Vérifier que le port n'est pas déjà utilisé
2. Vérifier la connexion MySQL
3. Vérifier que Kafka est démarré (pour facture/paiement/notification)
4. Vérifier les logs pour les erreurs spécifiques

### Problème: Service non visible dans Eureka

**Solutions**:
1. Vérifier que Eureka Server est démarré en premier
2. Vérifier l'URL dans `application.yml`
3. Attendre quelques secondes pour l'enregistrement

### Problème: Erreur de connexion Kafka

**Solutions**:
1. Vérifier que Kafka est démarré: `docker ps` ou vérifier les processus
2. Vérifier l'URL dans `application.yml`: `localhost:9092`
3. Vérifier que les topics existent (créés automatiquement)

### Problème: Erreur Feign Client

**Solutions**:
1. Vérifier que le service cible est enregistré dans Eureka
2. Vérifier le nom du service dans `@FeignClient`
3. Vérifier les logs pour les détails de l'erreur

## ✅ Checklist de validation

- [ ] Tous les services démarrent sans erreur
- [ ] Tous les services sont visibles dans Eureka
- [ ] Les bases de données sont créées automatiquement
- [ ] CRUD Client fonctionne
- [ ] CRUD Facture fonctionne
- [ ] Paiement fonctionne et met à jour le statut
- [ ] Notifications sont créées après paiement
- [ ] Batch de relance fonctionne (ou peut être déclenché)
- [ ] API Gateway route correctement les requêtes

## 📝 Notes importantes

1. **Ordre de démarrage**: Respecter l'ordre indiqué (Eureka en premier)
2. **Ports**: Vérifier que les ports ne sont pas déjà utilisés
3. **Kafka**: Les topics sont créés automatiquement au premier envoi
4. **Batch**: Le batch s'exécute automatiquement à minuit (modifier le cron pour tester)
5. **Email**: Configurer les credentials SMTP dans `notification-service/application.yml` pour les emails réels

## 🎯 Prochaines étapes

1. Créer le frontend Angular
2. Ajouter l'authentification JWT
3. Ajouter des tests unitaires et d'intégration
4. Configurer Docker Compose pour tout démarrer en une commande
5. Ajouter Prometheus et Grafana pour le monitoring

