# Documentation - Application de Gestion de Factures et Paiements

## 📋 Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Guide d'installation](#guide-dinstallation)
4. [Guide de démarrage](#guide-de-démarrage)
5. [API Documentation](#api-documentation)
6. [Diagrammes](#diagrammes)
7. [Tests](#tests)

## Vue d'ensemble

Cette application est une plateforme distribuée de gestion de factures et paiements basée sur une architecture microservices avec Spring Cloud et Angular.

### Technologies utilisées

- **Backend**: Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config Server
- **Communication synchrone**: OpenFeign
- **Communication asynchrone**: Apache Kafka
- **Base de données**: MySQL
- **Batch Processing**: Spring Batch
- **Frontend**: Angular (à créer)

## Architecture

### Composants principaux

1. **Eureka Server** (Port 8761) - Service Discovery
2. **Config Server** (Port 8888) - Configuration centralisée
3. **API Gateway** (Port 8085) - Point d'entrée unique
4. **Client Service** (Port 8081) - Gestion des clients
5. **Facture Service** (Port 8082) - Gestion des factures + Batch
6. **Paiement Service** (Port 8083) - Gestion des paiements
7. **Notification Service** (Port 8084) - Notifications asynchrones

### Bases de données

- `client_db` - Base de données du service Client
- `facture_db` - Base de données du service Facture
- `paiement_db` - Base de données du service Paiement
- `notification_db` - Base de données du service Notification

## Guide d'installation

### Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- MySQL 8.0+
- Apache Kafka 2.8+ (ou Docker)
- Node.js 18+ et npm (pour Angular)

### Installation des dépendances

#### 1. MySQL

```bash
# Installer MySQL et créer les bases de données
mysql -u root -p

CREATE DATABASE client_db;
CREATE DATABASE facture_db;
CREATE DATABASE paiement_db;
CREATE DATABASE notification_db;
```

#### 2. Apache Kafka

**Option A: Avec Docker**
```bash
docker-compose up -d kafka zookeeper
```

**Option B: Installation locale**
- Télécharger Kafka depuis https://kafka.apache.org/downloads
- Démarrer Zookeeper et Kafka

#### 3. Build du projet

```bash
# À la racine du projet
mvn clean install
```

## Guide de démarrage

### Ordre de démarrage des services

1. **Eureka Server**
```bash
cd backend/eureka-server
mvn spring-boot:run
```
Vérifier: http://localhost:8761

2. **Config Server**
```bash
cd backend/config-server
mvn spring-boot:run
```

3. **Client Service**
```bash
cd backend/client-service
mvn spring-boot:run
```

4. **Facture Service**
```bash
cd backend/facture-service
mvn spring-boot:run
```

5. **Paiement Service**
```bash
cd backend/paiement-service
mvn spring-boot:run
```

6. **Notification Service**
```bash
cd backend/notification-service
mvn spring-boot:run
```

7. **API Gateway**
```bash
cd backend/api-gateway
mvn spring-boot:run
```

### Scripts de démarrage

Utiliser les scripts fournis dans le dossier `scripts/`:
- `start-all.sh` (Linux/Mac)
- `start-all.bat` (Windows)

## API Documentation

### Endpoints via API Gateway (http://localhost:8085)

#### Client Service

- `GET /api/clients` - Liste tous les clients
- `GET /api/clients/{id}` - Obtenir un client par ID
- `POST /api/clients` - Créer un client
- `PUT /api/clients/{id}` - Mettre à jour un client
- `DELETE /api/clients/{id}` - Supprimer un client
- `GET /api/clients/search?nom=...` - Rechercher par nom
- `GET /api/clients/search?email=...` - Rechercher par email

#### Facture Service

- `GET /api/factures` - Liste toutes les factures
- `GET /api/factures/{id}` - Obtenir une facture par ID
- `GET /api/factures/client/{clientId}` - Factures d'un client
- `POST /api/factures` - Créer une facture
- `PUT /api/factures/{id}/status?statut=PAYEE` - Mettre à jour le statut
- `GET /api/factures/statut/{statut}` - Factures par statut

#### Paiement Service

- `POST /api/paiements` - Effectuer un paiement
- `GET /api/paiements/{id}` - Obtenir un paiement par ID
- `GET /api/paiements/facture/{factureId}` - Paiements d'une facture
- `GET /api/paiements` - Liste tous les paiements

#### Notification Service

- `GET /api/notifications` - Liste toutes les notifications
- `GET /api/notifications/client/{email}` - Notifications d'un client

## Diagrammes

Voir les fichiers dans le dossier `diagrammes/`:
- `architecture-microservices.md`
- `sequence-paiement.md`
- `batch-relance.md`

## Tests

### Tests manuels avec cURL

#### Créer un client
```bash
curl -X POST http://localhost:8085/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Jean Dupont",
    "email": "jean.dupont@example.com",
    "telephone": "0123456789",
    "adresse": "123 Rue Example"
  }'
```

#### Créer une facture
```bash
curl -X POST http://localhost:8085/api/factures \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "montant": 1000.00,
    "dateEmission": "2024-01-15",
    "dateEcheance": "2024-02-15",
    "description": "Facture de services"
  }'
```

#### Effectuer un paiement
```bash
curl -X POST http://localhost:8085/api/paiements \
  -H "Content-Type: application/json" \
  -d '{
    "factureId": 1,
    "montant": 1000.00,
    "methodePaiement": "VIREMENT"
  }'
```

## Support

Pour toute question ou problème, consulter les logs des services ou la documentation détaillée dans ce dossier.

