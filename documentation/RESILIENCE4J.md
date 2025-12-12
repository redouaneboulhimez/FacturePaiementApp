# 🔄 Configuration Resilience4J

## Vue d'ensemble

Resilience4J est intégré dans les microservices pour gérer la résilience des appels Feign avec :
- **Circuit Breaker** : Protège contre les cascades de défaillances
- **Retry** : Réessaie automatiquement les appels échoués
- **Time Limiter** : Limite le temps d'attente des appels
- **Fallback** : Fournit des réponses alternatives en cas d'échec

## Services configurés

### 1. Paiement Service
- Circuit Breaker pour `facture-service`
- Circuit Breaker pour `client-service`
- Fallbacks configurés

### 2. Notification Service
- Circuit Breaker pour `client-service`
- Fallback configuré

## Configuration

### Paramètres du Circuit Breaker

```yaml
resilience4j:
  circuitbreaker:
    instances:
      factureService:
        slidingWindowSize: 10              # Nombre d'appels dans la fenêtre
        minimumNumberOfCalls: 5           # Minimum d'appels avant évaluation
        failureRateThreshold: 50           # Taux d'échec pour ouvrir (50%)
        waitDurationInOpenState: 10s       # Temps avant tentative half-open
        permittedNumberOfCallsInHalfOpenState: 3  # Appels en half-open
        automaticTransitionFromOpenToHalfOpenEnabled: true
```

### Paramètres du Retry

```yaml
resilience4j:
  retry:
    instances:
      factureService:
        maxAttempts: 3                     # Nombre maximum de tentatives
        waitDuration: 1000                # Délai entre tentatives (ms)
        retryExceptions:                  # Exceptions qui déclenchent retry
          - org.springframework.web.client.HttpServerErrorException
          - java.net.SocketTimeoutException
```

### Paramètres du Time Limiter

```yaml
resilience4j:
  timelimiter:
    instances:
      factureService:
        timeoutDuration: 3s                # Timeout maximum
```

## États du Circuit Breaker

1. **CLOSED** (Fermé) : Fonctionne normalement
2. **OPEN** (Ouvert) : Trop d'échecs, utilise le fallback
3. **HALF_OPEN** (Mi-ouvert) : Teste si le service est revenu

## Fallbacks implémentés

### FactureFeignClientFallback
```java
// Retourne une réponse avec un message d'erreur
// Empêche l'application de planter si facture-service est down
```

### ClientFeignClientFallback
```java
// Retourne une réponse avec un message d'erreur
// Utilise un email vide si client-service est down
```

## Monitoring

### Actuator Endpoints

Les métriques sont disponibles via Actuator :

```bash
# État des circuit breakers
GET http://localhost:8083/actuator/circuitbreakers

# Métriques
GET http://localhost:8083/actuator/metrics/resilience4j.circuitbreaker.calls

# Health check avec circuit breaker
GET http://localhost:8083/actuator/health
```

### Métriques disponibles

- `resilience4j.circuitbreaker.calls` : Nombre d'appels
- `resilience4j.circuitbreaker.state` : État actuel
- `resilience4j.retry.calls` : Nombre de retries
- `resilience4j.timelimiter.calls` : Appels avec timeout

## Comportement en cas d'échec

### Scénario 1: Service indisponible

1. Circuit Breaker détecte les échecs
2. Après 50% d'échecs sur 10 appels, le circuit s'ouvre
3. Les appels suivants utilisent automatiquement le fallback
4. Après 10 secondes, le circuit passe en HALF_OPEN
5. Si les tests réussissent, le circuit se ferme

### Scénario 2: Timeout

1. Time Limiter déclenche un timeout après 3 secondes
2. Retry réessaie jusqu'à 3 fois
3. Si tous les retries échouent, le fallback est utilisé

### Scénario 3: Erreur réseau

1. Retry réessaie automatiquement
2. Si les retries échouent, le Circuit Breaker s'ouvre
3. Le fallback est utilisé

## Exemple d'utilisation

### Dans PaiementService

```java
@Autowired
private FactureFeignClient factureFeignClient; // Avec fallback automatique

public Paiement effectuerPaiement(...) {
    // Si facture-service est down, le fallback retourne une réponse d'erreur
    Map<String, Object> facture = factureFeignClient.getFactureById(id);
    
    // Vérifier si c'est une réponse de fallback
    if (facture.containsKey("error")) {
        throw new RuntimeException("Service indisponible: " + facture.get("message"));
    }
    
    // Continuer le traitement normal...
}
```

## Tests

### Tester le Circuit Breaker

1. Démarrer tous les services
2. Arrêter `facture-service`
3. Effectuer plusieurs appels via `paiement-service`
4. Observer les logs : "Circuit breaker ouvert - Fallback activé"
5. Vérifier les métriques via Actuator

### Tester le Retry

1. Simuler une latence élevée dans `facture-service`
2. Effectuer un appel via `paiement-service`
3. Observer les retries dans les logs
4. Vérifier que le fallback est utilisé après 3 tentatives

## Bonnes pratiques

1. **Configurer des timeouts raisonnables** : 3-5 secondes max
2. **Ajuster le failureRateThreshold** : 50% est un bon point de départ
3. **Implémenter des fallbacks utiles** : Retourner des valeurs par défaut plutôt que null
4. **Monitorer les métriques** : Surveiller les ouvertures de circuit
5. **Tester les scénarios d'échec** : Vérifier que les fallbacks fonctionnent

## Configuration avancée

### Personnaliser par endpoint

Vous pouvez créer des configurations spécifiques pour différents endpoints :

```yaml
resilience4j:
  circuitbreaker:
    instances:
      factureServiceGet:
        failureRateThreshold: 30
      factureServiceUpdate:
        failureRateThreshold: 70
```

### Activer les événements

```yaml
resilience4j:
  circuitbreaker:
    instances:
      factureService:
        eventConsumerBufferSize: 10  # Stocke les 10 derniers événements
```

## Dépannage

### Le circuit breaker ne s'ouvre pas

- Vérifier que `minimumNumberOfCalls` est atteint
- Vérifier que `failureRateThreshold` est dépassé
- Vérifier les logs pour les erreurs

### Le fallback n'est pas appelé

- Vérifier que `feign.circuitbreaker.enabled: true`
- Vérifier que la classe de fallback est annotée `@Component`
- Vérifier que le nom du fallback correspond dans `@FeignClient`

### Timeout trop court

- Augmenter `timeoutDuration` dans la configuration
- Vérifier la latence réseau réelle

## Références

- [Documentation Resilience4J](https://resilience4j.readme.io/)
- [Spring Cloud Circuit Breaker](https://spring.io/projects/spring-cloud-circuitbreaker)

