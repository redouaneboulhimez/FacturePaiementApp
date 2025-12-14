# 🔧 Guide de Dépannage Rapide

## Problème : Aucune information n'apparaît sur les pages

### Solution 1 : Redémarrer l'API Gateway avec la configuration CORS

L'API Gateway a été mis à jour avec une configuration CORS. **Il doit être redémarré** pour que les changements prennent effet.

#### Étape 1 : Arrêter l'API Gateway
- Trouvez la fenêtre de terminal où l'API Gateway est en cours d'exécution
- Appuyez sur `Ctrl+C` pour l'arrêter

#### Étape 2 : Redémarrer l'API Gateway
```bash
cd backend/api-gateway
mvn spring-boot:run
```

**OU** utilisez le script de démarrage :
```bash
scripts\start-all.bat
```

### Solution 2 : Vérifier que tous les services sont démarrés

Vérifiez que tous les services sont en cours d'exécution :

1. **Eureka Server** (port 8761) : http://localhost:8761
   - Vérifiez que tous les services sont enregistrés

2. **API Gateway** (port 8085) : http://localhost:8085/actuator/health
   - Doit retourner `{"status":"UP"}`

3. **Services backend** :
   - Client Service (port 8081)
   - Facture Service (port 8082)
   - Paiement Service (port 8083)
   - Notification Service (port 8084)

### Solution 3 : Vérifier les erreurs dans la console du navigateur

1. Ouvrez la console du navigateur (F12)
2. Allez dans l'onglet "Console"
3. Rechargez la page
4. Vérifiez s'il y a des erreurs CORS ou de connexion

### Solution 4 : Tester les APIs directement

Testez si les APIs fonctionnent :

```powershell
# Tester l'API Clients
Invoke-WebRequest -Uri "http://localhost:8085/api/clients" -UseBasicParsing

# Tester l'API Factures
Invoke-WebRequest -Uri "http://localhost:8085/api/factures" -UseBasicParsing
```

Si ces commandes fonctionnent mais que le frontend ne charge pas les données, c'est un problème CORS qui sera résolu après le redémarrage de l'API Gateway.

## Vérification rapide

### Checklist de démarrage

- [ ] Docker est démarré (MySQL, Kafka, Zookeeper)
- [ ] Eureka Server est démarré (port 8761)
- [ ] Config Server est démarré (port 8888)
- [ ] Tous les microservices sont démarrés (ports 8081-8084)
- [ ] **API Gateway est redémarré avec la nouvelle configuration CORS** (port 8085)
- [ ] Frontend Angular est démarré (port 4200)

### Vérifier Eureka Dashboard

Ouvrez http://localhost:8761 dans votre navigateur.

Vous devriez voir 6 services enregistrés :
- API-GATEWAY
- CLIENT-SERVICE
- FACTURE-SERVICE
- PAIEMENT-SERVICE
- NOTIFICATION-SERVICE
- CONFIG-SERVER

## Messages d'erreur courants

### "Impossible de se connecter au serveur"
- **Cause** : L'API Gateway n'est pas démarré ou n'est pas accessible
- **Solution** : Vérifiez que l'API Gateway est démarré sur le port 8085

### "0 Unknown Error"
- **Cause** : Problème CORS ou connexion refusée
- **Solution** : Redémarrez l'API Gateway avec la nouvelle configuration CORS

### "Cette facture a déjà été payée"
- **Cause** : Vous essayez de payer une facture déjà payée
- **Solution** : Sélectionnez une facture avec le statut "EN_ATTENTE"

## Commandes utiles

### Vérifier les ports utilisés
```powershell
netstat -ano | findstr ":8081 :8082 :8083 :8084 :8085 :8761"
```

### Vérifier les services Docker
```powershell
docker ps
```

### Redémarrer tous les services
```bash
scripts\start-all.bat
```

## Après le redémarrage de l'API Gateway

1. Attendez 30 secondes que l'API Gateway démarre complètement
2. Rafraîchissez la page dans le navigateur (F5)
3. Ouvrez la console du navigateur (F12) pour vérifier les erreurs
4. Les données devraient maintenant s'afficher correctement

## Support

Si le problème persiste après avoir suivi ces étapes :
1. Vérifiez les logs de l'API Gateway
2. Vérifiez les logs dans la console du navigateur
3. Vérifiez que tous les services sont bien enregistrés dans Eureka

