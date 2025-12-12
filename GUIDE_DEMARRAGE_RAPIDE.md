# 🚀 Guide de Démarrage Rapide

Guide complet pour démarrer l'application Facture Paiement en quelques minutes.

## 📋 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- ✅ **Java 17+** (vous avez Java 21 ✅)
- ✅ **Maven 3.6+** (via IntelliJ IDEA ✅)
- ✅ **Docker Desktop** - [Télécharger](https://www.docker.com/products/docker-desktop)
- ✅ **Node.js 18+ et npm** - [Télécharger](https://nodejs.org/) (pour Angular)

## 🔧 Configuration de l'environnement

### Configuration automatique (Recommandé)

Exécutez le script de configuration :

```cmd
scripts\setup-env.bat
```

Ce script configure automatiquement :
- `JAVA_HOME` vers votre installation Java
- `PATH` pour inclure Java et Maven

### Configuration manuelle (si le script ne fonctionne pas)

Dans PowerShell, exécutez :

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:PATH += ";C:\Program Files\JetBrains\IntelliJ IDEA 2025.1.1.1\plugins\maven\lib\maven3\bin"
```

**Note :** Ces variables sont définies pour la session actuelle uniquement. Pour les rendre permanentes, ajoutez-les dans les Variables d'environnement Windows.

## 📦 Étape 1 : Démarrer MySQL et Kafka avec Docker

```cmd
docker-compose up -d
```

Cette commande démarre :
- ✅ MySQL (port 3306) avec toutes les bases de données créées automatiquement
- ✅ Zookeeper (port 2181)
- ✅ Kafka (port 9092)

**Vérification :**
```cmd
docker ps
```
Vous devriez voir 3 conteneurs en cours d'exécution.

**Vérifier les bases de données MySQL :**
```cmd
docker exec -it mysql-all-databases mysql -u root -proot -e "SHOW DATABASES;"
```

Vous devriez voir : `client_db`, `facture_db`, `paiement_db`, `notification_db`

## 🔨 Étape 2 : Build du projet backend

**Important :** Assurez-vous d'avoir exécuté `scripts\setup-env.bat` ou configuré les variables d'environnement.

```cmd
mvn clean install -DskipTests
```

Cette commande compile tous les microservices. Cela peut prendre quelques minutes la première fois.

## 🚀 Étape 3 : Démarrer les microservices backend

### Option A : Script automatique (Recommandé)

**Windows :**
```cmd
scripts\start-all.bat
```

**Linux/Mac :**
```bash
chmod +x scripts/start-all.sh
./scripts/start-all.sh
```

### Option B : Démarrage manuel

Ouvrez **7 terminaux** et exécutez dans l'ordre :

**Terminal 1 - Eureka Server**
```cmd
cd backend\eureka-server
mvn spring-boot:run
```
👉 Vérifier : http://localhost:8761

**Terminal 2 - Config Server**
```cmd
cd backend\config-server
mvn spring-boot:run
```

**Terminal 3 - Client Service**
```cmd
cd backend\client-service
mvn spring-boot:run
```

**Terminal 4 - Facture Service**
```cmd
cd backend\facture-service
mvn spring-boot:run
```

**Terminal 5 - Paiement Service**
```cmd
cd backend\paiement-service
mvn spring-boot:run
```

**Terminal 6 - Notification Service**
```cmd
cd backend\notification-service
mvn spring-boot:run
```

**Terminal 7 - API Gateway**
```cmd
cd backend\api-gateway
mvn spring-boot:run
```

### Vérifier que tous les services sont démarrés

1. **Ouvrir Eureka Dashboard** : http://localhost:8761
   - Vous devriez voir 6 services enregistrés

2. **Tester l'API Gateway** :
```cmd
curl http://localhost:8080/api/clients
```

## 🎨 Étape 4 : Démarrer le frontend Angular

### Installer les dépendances (première fois uniquement)
```cmd
cd frontend
npm install
```

### Démarrer le serveur de développement
```cmd
npm start
```

Le frontend sera accessible sur : **http://localhost:4200**

## ✅ Étape 5 : Vérification complète

### Vérifier tous les services

| Service | URL | Statut |
|---------|-----|--------|
| Eureka Dashboard | http://localhost:8761 | ✅ |
| API Gateway | http://localhost:8080 | ✅ |
| Frontend Angular | http://localhost:4200 | ✅ |
| MySQL | localhost:3306 | ✅ (via Docker) |
| Kafka | localhost:9092 | ✅ (via Docker) |

### Test rapide de l'application

1. **Ouvrir le frontend** : http://localhost:4200
2. **Créer un client** :
   - Aller dans "Clients"
   - Cliquer sur "Nouveau Client"
   - Remplir le formulaire et créer

3. **Créer une facture** :
   - Aller dans "Factures"
   - Cliquer sur "Nouvelle Facture"
   - Sélectionner le client créé
   - Remplir et créer

4. **Effectuer un paiement** :
   - Aller dans "Paiements"
   - Cliquer sur "Nouveau Paiement"
   - Sélectionner la facture créée
   - Cliquer sur "Payer"

5. **Vérifier les notifications** :
   - Aller dans "Notifications"
   - Vous devriez voir une notification de paiement

## 🧪 Tests avec cURL (alternative)

### Créer un client
```cmd
curl -X POST http://localhost:8080/api/clients -H "Content-Type: application/json" -d "{\"nom\":\"Jean Dupont\",\"email\":\"jean@example.com\",\"telephone\":\"0123456789\",\"adresse\":\"123 Rue Example\"}"
```

### Créer une facture
```cmd
curl -X POST http://localhost:8080/api/factures -H "Content-Type: application/json" -d "{\"clientId\":1,\"montant\":1500.00,\"dateEmission\":\"2024-01-15\",\"dateEcheance\":\"2024-02-15\",\"description\":\"Facture de services\"}"
```

### Effectuer un paiement
```cmd
curl -X POST http://localhost:8080/api/paiements -H "Content-Type: application/json" -d "{\"factureId\":1,\"montant\":1500.00,\"methodePaiement\":\"VIREMENT\"}"
```

## 🛑 Arrêter l'application

### Arrêter les services backend
- Fermer les fenêtres de terminal ou utiliser `Ctrl+C` dans chaque terminal

### Arrêter le frontend
Dans le terminal Angular : `Ctrl+C`

### Arrêter Docker
```cmd
docker-compose down
```

Pour supprimer aussi les volumes (données) :
```cmd
docker-compose down -v
```

## 🐛 Dépannage rapide

### Maven non trouvé
```cmd
REM Exécuter le script de configuration
scripts\setup-env.bat

REM Ou configurer manuellement dans PowerShell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:PATH += ";C:\Program Files\JetBrains\IntelliJ IDEA 2025.1.1.1\plugins\maven\lib\maven3\bin"
```

### Port déjà utilisé
```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Service ne démarre pas
1. Vérifier les logs dans la console
2. Vérifier que MySQL et Kafka sont démarrés : `docker ps`
3. Vérifier que le port n'est pas déjà utilisé

### Frontend ne se connecte pas à l'API
1. Vérifier que l'API Gateway est démarré : http://localhost:8080
2. Vérifier les erreurs dans la console du navigateur (F12)

### MySQL ne démarre pas
```cmd
docker logs mysql-all-databases
docker-compose restart mysql
```

## 📊 Ordre de démarrage recommandé

1. ✅ **Configurer l'environnement** - `scripts\setup-env.bat`
2. ✅ **Docker** (MySQL + Kafka) - `docker-compose up -d`
3. ✅ **Eureka Server** - Port 8761
4. ✅ **Config Server** - Port 8888
5. ✅ **Client Service** - Port 8081
6. ✅ **Facture Service** - Port 8082
7. ✅ **Paiement Service** - Port 8083
8. ✅ **Notification Service** - Port 8084
9. ✅ **API Gateway** - Port 8080
10. ✅ **Frontend Angular** - Port 4200

## 🎯 Checklist de démarrage

- [ ] Docker Desktop est démarré
- [ ] `docker-compose up -d` exécuté avec succès
- [ ] MySQL accessible (3 conteneurs visibles avec `docker ps`)
- [ ] Environnement configuré (`scripts\setup-env.bat`)
- [ ] `mvn clean install` terminé sans erreur
- [ ] Eureka Dashboard accessible : http://localhost:8761
- [ ] Tous les services visibles dans Eureka (6 services)
- [ ] API Gateway répond : http://localhost:8080/api/clients
- [ ] Frontend Angular démarré : http://localhost:4200
- [ ] Test de création d'un client réussi
- [ ] Test de création d'une facture réussi
- [ ] Test de paiement réussi
- [ ] Notification visible dans l'interface

## 📚 Documentation supplémentaire

Pour plus de détails, consultez :
- **Guide complet** : `documentation/GUIDE_LAB.md`
- **Référence API** : `documentation/API_REFERENCE.md`
- **Dépannage** : `documentation/TROUBLESHOOTING.md`
- **Resilience4J** : `documentation/RESILIENCE4J.md`

## 🎉 C'est parti !

Votre application est maintenant prête à être utilisée. Bon développement ! 🚀

