# 🔧 Guide de Dépannage

## Problèmes courants et solutions

### 1. Service ne démarre pas

#### Symptôme
```
Error: Port XXXX is already in use
```

#### Solution
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

#### Vérifier les ports utilisés
- Eureka: 8761
- Config Server: 8888
- Client Service: 8081
- Facture Service: 8082
- Paiement Service: 8083
- Notification Service: 8084
- API Gateway: 8080

---

### 2. Service non visible dans Eureka

#### Symptôme
Le service démarre mais n'apparaît pas dans le dashboard Eureka (http://localhost:8761)

#### Solutions

1. **Vérifier l'ordre de démarrage**
   - Eureka Server doit être démarré en premier
   - Attendre 10-15 secondes après le démarrage d'Eureka

2. **Vérifier la configuration**
   ```yaml
   eureka:
     client:
       service-url:
         defaultZone: http://localhost:8761/eureka/
   ```

3. **Vérifier les logs**
   ```
   Look for: "Registered with Eureka"
   ```

4. **Vérifier le nom du service**
   ```yaml
   spring:
     application:
       name: client-service  # Doit correspondre exactement
   ```

---

### 3. Erreur de connexion MySQL

#### Symptôme
```
Communications link failure
Access denied for user 'root'@'localhost'
```

#### Solutions

1. **Vérifier que MySQL est démarré**
   ```bash
   # Windows
   net start MySQL80
   
   # Linux/Mac
   sudo systemctl start mysql
   ```

2. **Vérifier les credentials**
   ```yaml
   spring:
     datasource:
       username: root
       password: root  # Votre mot de passe MySQL
   ```

3. **Créer les bases de données**
   ```sql
   CREATE DATABASE client_db;
   CREATE DATABASE facture_db;
   CREATE DATABASE paiement_db;
   CREATE DATABASE notification_db;
   ```

4. **Vérifier les permissions**
   ```sql
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost';
   FLUSH PRIVILEGES;
   ```

---

### 4. Erreur Kafka

#### Symptôme
```
Failed to connect to broker localhost:9092
```

#### Solutions

1. **Vérifier que Kafka est démarré**
   ```bash
   # Avec Docker
   docker ps | grep kafka
   
   # Vérifier les logs
   docker logs kafka
   ```

2. **Démarrer Kafka avec Docker**
   ```bash
   docker-compose up -d kafka zookeeper
   ```

3. **Vérifier la configuration**
   ```yaml
   spring:
     kafka:
       bootstrap-servers: localhost:9092
   ```

4. **Vérifier que Zookeeper est démarré** (Kafka en dépend)

---

### 5. Erreur Feign Client

#### Symptôme
```
Load balancer does not have available server for client: facture-service
```

#### Solutions

1. **Vérifier que le service cible est enregistré dans Eureka**
   - Ouvrir http://localhost:8761
   - Vérifier que le service apparaît

2. **Vérifier le nom du service dans @FeignClient**
   ```java
   @FeignClient(name = "facture-service")  // Doit correspondre au nom dans Eureka
   ```

3. **Attendre quelques secondes** après le démarrage du service

4. **Vérifier les logs du service cible** pour des erreurs

---

### 6. Batch ne s'exécute pas

#### Symptôme
Le batch de relance ne s'exécute pas automatiquement

#### Solutions

1. **Vérifier que @EnableScheduling est activé**
   ```java
   @SpringBootApplication
   @EnableScheduling  // Doit être présent
   ```

2. **Vérifier le cron expression**
   ```java
   @Scheduled(cron = "0 0 0 * * ?")  // Minuit tous les jours
   ```

3. **Pour tester immédiatement**, modifier temporairement:
   ```java
   @Scheduled(fixedRate = 60000)  // Toutes les minutes (pour test)
   ```

4. **Vérifier les logs** pour des erreurs d'exécution

---

### 7. Notifications non envoyées

#### Symptôme
Les paiements fonctionnent mais aucune notification n'est créée

#### Solutions

1. **Vérifier que Kafka fonctionne**
   ```bash
   docker logs kafka
   ```

2. **Vérifier que le listener est actif**
   ```java
   @KafkaListener(topics = "paiement-topic", groupId = "notification-group")
   ```

3. **Vérifier les logs du Notification Service**
   - Chercher "Consuming message" ou des erreurs

4. **Vérifier la configuration email** (si emails réels)
   ```yaml
   spring:
     mail:
       username: your-email@gmail.com
       password: your-app-password
   ```

---

### 8. Erreur de compilation Maven

#### Symptôme
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin
```

#### Solutions

1. **Vérifier la version Java**
   ```bash
   java -version  # Doit être 17 ou supérieur
   ```

2. **Nettoyer et rebuild**
   ```bash
   mvn clean install
   ```

3. **Vérifier JAVA_HOME**
   ```bash
   echo $JAVA_HOME  # Linux/Mac
   echo %JAVA_HOME%  # Windows
   ```

---

### 9. Erreur CORS

#### Symptôme
```
Access to XMLHttpRequest has been blocked by CORS policy
```

#### Solution
Les controllers ont déjà `@CrossOrigin(origins = "*")`. Si le problème persiste:

```java
@CrossOrigin(origins = "*")
@RestController
public class ClientController {
    // ...
}
```

---

### 10. Erreur de validation

#### Symptôme
```
Validation failed for object='client'
```

#### Solutions

1. **Vérifier les champs obligatoires**
   - `nom` - obligatoire
   - `email` - obligatoire et format email valide

2. **Vérifier les formats**
   - Email: format email valide
   - Montant: nombre décimal positif

---

## Commandes utiles

### Vérifier les services en cours d'exécution

```bash
# Windows
netstat -ano | findstr :8080

# Linux/Mac
lsof -i :8080
```

### Vérifier les logs

```bash
# Logs d'un service spécifique
tail -f logs/client-service.log

# Logs de tous les services
tail -f logs/*.log
```

### Redémarrer un service

```bash
# Arrêter
kill <PID>

# Redémarrer
cd backend/client-service
mvn spring-boot:run
```

### Vérifier la connexion à MySQL

```bash
mysql -u root -p -e "SHOW DATABASES;"
```

### Vérifier Kafka

```bash
# Lister les topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Consulter les messages
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic paiement-topic --from-beginning
```

## Logs à surveiller

### Eureka Server
```
Registered instance CLIENT-SERVICE with status UP
```

### Services
```
Started ClientServiceApplication
Registered with Eureka
```

### Kafka
```
Successfully sent message to topic: paiement-topic
```

### Batch
```
Job: [SimpleJob: [name=relanceJob]] launched
```

## Support supplémentaire

Si le problème persiste:

1. Vérifier les logs complets de tous les services
2. Vérifier la version de Java (doit être 17+)
3. Vérifier la version de Maven (doit être 3.6+)
4. Vérifier que tous les ports sont disponibles
5. Vérifier la connexion réseau (localhost)
6. Consulter la documentation Spring Cloud pour votre version

