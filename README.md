# 💰 Application de Gestion de Factures et Paiements

Application distribuée basée sur une architecture microservices avec Spring Cloud et Angular.

## 🏗️ Architecture

Cette application suit une architecture microservices avec les composants suivants:

- **Eureka Server** - Service Discovery (Port 8761)
- **Config Server** - Configuration centralisée (Port 8888)
- **API Gateway** - Point d'entrée unique (Port 8085)
- **Client Service** - Gestion des clients (Port 8081)
- **Facture Service** - Gestion des factures + Batch (Port 8082)
- **Paiement Service** - Gestion des paiements (Port 8083)
- **Notification Service** - Notifications asynchrones (Port 8084)
- **Frontend Angular** - Interface utilisateur (Port 4200)

## 🚀 Démarrage rapide

### Prérequis

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Apache Kafka (ou Docker)
- Node.js 18+ et npm (pour Angular)

### Installation

1. **Cloner le projet**
```bash
git clone <repository-url>
cd FacturePaiementAPP
```

2. **Créer les bases de données MySQL**
```sql
CREATE DATABASE client_db;
CREATE DATABASE facture_db;
CREATE DATABASE paiement_db;
CREATE DATABASE notification_db;
```

Ou utiliser le script:
```bash
mysql -u root -p < scripts/create-databases.sql
```

3. **Démarrer Kafka** (avec Docker)
```bash
docker-compose up -d kafka zookeeper
```

4. **Build le projet**
```bash
mvn clean install
```

5. **Démarrer les services**

**Option A: Scripts automatiques**
```bash
# Linux/Mac
chmod +x scripts/start-all.sh
./scripts/start-all.sh

# Windows
scripts\start-all.bat
```

**Option B: Manuellement (dans l'ordre)**
```bash
# Terminal 1
cd backend/eureka-server && mvn spring-boot:run

# Terminal 2
cd backend/config-server && mvn spring-boot:run

# Terminal 3
cd backend/client-service && mvn spring-boot:run

# Terminal 4
cd backend/facture-service && mvn spring-boot:run

# Terminal 5
cd backend/paiement-service && mvn spring-boot:run

# Terminal 6
cd backend/notification-service && mvn spring-boot:run

# Terminal 7
cd backend/api-gateway && mvn spring-boot:run
```

6. **Démarrer le frontend Angular**
```bash
cd frontend
npm install
npm start
```

7. **Vérifier le démarrage**
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8085
- Frontend Angular: http://localhost:4200

## 📚 Documentation

Toute la documentation détaillée se trouve dans le dossier `documentation/`:

- **[README.md](documentation/README.md)** - Documentation générale
- **[GUIDE_LAB.md](documentation/GUIDE_LAB.md)** - Guide étape par étape du laboratoire
- **[API_REFERENCE.md](documentation/API_REFERENCE.md)** - Référence complète de l'API
- **[TROUBLESHOOTING.md](documentation/TROUBLESHOOTING.md)** - Guide de dépannage
- **[RESILIENCE4J.md](documentation/RESILIENCE4J.md)** - Configuration Resilience4J
- **[RESUME_COMPLET.md](documentation/RESUME_COMPLET.md)** - Résumé complet du projet
- **[diagrammes/](documentation/diagrammes/)** - Diagrammes d'architecture et de séquence

## 🧪 Tests rapides

### Créer un client
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

### Créer une facture
```bash
curl -X POST http://localhost:8085/api/factures \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "montant": 1500.00,
    "dateEmission": "2024-01-15",
    "dateEcheance": "2024-02-15",
    "description": "Facture de services"
  }'
```

### Effectuer un paiement
```bash
curl -X POST http://localhost:8085/api/paiements \
  -H "Content-Type: application/json" \
  -d '{
    "factureId": 1,
    "montant": 1500.00,
    "methodePaiement": "VIREMENT"
  }'
```

## ✅ Fonctionnalités implémentées

- ✅ CRUD Clients
- ✅ CRUD Factures
- ✅ Paiement de factures
- ✅ Mise à jour automatique du statut
- ✅ Notifications asynchrones (Kafka)
- ✅ Batch de relance automatique (tous les jours à minuit)
- ✅ Service Discovery (Eureka)
- ✅ API Gateway avec routing
- ✅ Communication synchrone (Feign)
- ✅ Communication asynchrone (Kafka)
- ✅ **Resilience4J avec Circuit Breaker, Retry, Fallback**
- ✅ **Frontend Angular complet**

## 🔧 Technologies utilisées

### Backend
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- Spring Cloud Gateway
- Netflix Eureka
- Spring Cloud Config
- OpenFeign
- **Resilience4J 2.1.0** (Circuit Breaker, Retry, Time Limiter)
- Spring Batch
- Spring Kafka
- MySQL 8.0
- Spring Boot Actuator

### Frontend
- Angular 17+
- TypeScript 5.2+
- RxJS
- Standalone Components

### Infrastructure
- Apache Kafka
- MySQL
- Docker (optionnel)

## 📝 Structure du projet

```
FacturePaiementAPP/
├── backend/                # Tous les microservices backend
│   ├── eureka-server/      # Service Discovery
│   ├── config-server/      # Configuration centralisée
│   ├── api-gateway/        # API Gateway
│   ├── client-service/     # Microservice Client
│   ├── facture-service/    # Microservice Facture + Batch
│   ├── paiement-service/   # Microservice Paiement
│   └── notification-service/ # Microservice Notification
├── frontend/               # Frontend Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/  # Composants (Dashboard, Clients, etc.)
│   │   │   └── services/    # Service API
│   │   └── styles.css
│   └── package.json
├── documentation/          # Documentation complète
│   ├── RESILIENCE4J.md     # Documentation Resilience4J
│   └── ...
├── scripts/                # Scripts de démarrage
├── docker-compose.yml      # Configuration Docker (Kafka, MySQL)
└── pom.xml                 # POM parent Maven
```

## 🎯 Prochaines étapes (Optionnelles)

- [ ] Ajouter l'authentification JWT
- [ ] Ajouter des tests unitaires et d'intégration
- [ ] Configurer Prometheus et Grafana
- [ ] Dockeriser tous les services

## 🔄 Resilience4J

Le projet intègre Resilience4J pour la résilience des microservices :

- **Circuit Breaker** : Protège contre les cascades de défaillances
- **Retry** : Réessaie automatiquement les appels échoués
- **Time Limiter** : Limite le temps d'attente
- **Fallback** : Réponses alternatives en cas d'échec

Voir `documentation/RESILIENCE4J.md` pour plus de détails.

## 🎨 Frontend Angular

Le frontend Angular inclut :

- **Dashboard** : Statistiques en temps réel
- **Gestion Clients** : CRUD complet
- **Gestion Factures** : Création et consultation
- **Paiements** : Interface de paiement
- **Notifications** : Liste des notifications

Voir `frontend/README.md` pour plus de détails.

## 🐛 Dépannage

Consulter la section [Dépannage](documentation/TROUBLESHOOTING.md) dans le guide du laboratoire.

## 📄 Licence

Ce projet est un projet éducatif.

## 👥 Auteurs

Développé dans le cadre d'un projet de laboratoire sur les microservices.
