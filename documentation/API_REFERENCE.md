# 📡 Référence API

## Base URL

Toutes les requêtes passent par l'API Gateway: `http://localhost:8085`

## Endpoints

### Client Service

#### Liste tous les clients
```http
GET /api/clients
```

**Réponse:**
```json
[
  {
    "id": 1,
    "nom": "Jean Dupont",
    "email": "jean.dupont@example.com",
    "telephone": "0123456789",
    "adresse": "123 Rue Example"
  }
]
```

#### Obtenir un client par ID
```http
GET /api/clients/{id}
```

**Réponse:**
```json
{
  "id": 1,
  "nom": "Jean Dupont",
  "email": "jean.dupont@example.com",
  "telephone": "0123456789",
  "adresse": "123 Rue Example"
}
```

#### Créer un client
```http
POST /api/clients
Content-Type: application/json

{
  "nom": "Jean Dupont",
  "email": "jean.dupont@example.com",
  "telephone": "0123456789",
  "adresse": "123 Rue Example"
}
```

#### Mettre à jour un client
```http
PUT /api/clients/{id}
Content-Type: application/json

{
  "nom": "Jean Dupont",
  "email": "jean.dupont@example.com",
  "telephone": "0123456789",
  "adresse": "456 Nouvelle Adresse"
}
```

#### Supprimer un client
```http
DELETE /api/clients/{id}
```

#### Rechercher des clients
```http
GET /api/clients/search?nom=Dupont
GET /api/clients/search?email=jean@example.com
```

---

### Facture Service

#### Liste toutes les factures
```http
GET /api/factures
```

**Réponse:**
```json
[
  {
    "id": 1,
    "clientId": 1,
    "montant": 1500.00,
    "dateEmission": "2024-01-15",
    "dateEcheance": "2024-02-15",
    "statut": "EN_ATTENTE",
    "description": "Facture de services"
  }
]
```

#### Obtenir une facture par ID
```http
GET /api/factures/{id}
```

#### Obtenir les factures d'un client
```http
GET /api/factures/client/{clientId}
```

#### Créer une facture
```http
POST /api/factures
Content-Type: application/json

{
  "clientId": 1,
  "montant": 1500.00,
  "dateEmission": "2024-01-15",
  "dateEcheance": "2024-02-15",
  "description": "Facture de services"
}
```

#### Mettre à jour le statut d'une facture
```http
PUT /api/factures/{id}/status?statut=PAYEE
```

**Statuts possibles:**
- `EN_ATTENTE`
- `PAYEE`
- `EN_RETARD`

#### Obtenir les factures par statut
```http
GET /api/factures/statut/EN_ATTENTE
GET /api/factures/statut/PAYEE
GET /api/factures/statut/EN_RETARD
```

---

### Paiement Service

#### Effectuer un paiement
```http
POST /api/paiements
Content-Type: application/json

{
  "factureId": 1,
  "montant": 1500.00,
  "methodePaiement": "VIREMENT"
}
```

**Réponse:**
```json
{
  "id": 1,
  "factureId": 1,
  "montant": 1500.00,
  "datePaiement": "2024-01-20T10:30:00",
  "methodePaiement": "VIREMENT"
}
```

**Note:** Ce paiement déclenche automatiquement:
- Mise à jour du statut de la facture à `PAYEE`
- Envoi d'un message Kafka
- Création d'une notification

#### Obtenir un paiement par ID
```http
GET /api/paiements/{id}
```

#### Obtenir les paiements d'une facture
```http
GET /api/paiements/facture/{factureId}
```

#### Liste tous les paiements
```http
GET /api/paiements
```

---

### Notification Service

#### Liste toutes les notifications
```http
GET /api/notifications
```

**Réponse:**
```json
[
  {
    "id": 1,
    "paiementId": 1,
    "factureId": 1,
    "clientEmail": "jean.dupont@example.com",
    "montant": 1500.00,
    "type": "PAIEMENT",
    "dateEnvoi": "2024-01-20T10:30:05",
    "statut": "ENVOYE",
    "message": "Notification de paiement envoyée avec succès"
  }
]
```

#### Obtenir les notifications d'un client
```http
GET /api/notifications/client/{email}
```

**Types de notifications:**
- `PAIEMENT` - Notification de paiement effectué
- `RELANCE` - Notification de relance pour facture impayée

---

## Codes de statut HTTP

- `200 OK` - Requête réussie
- `201 Created` - Ressource créée avec succès
- `204 No Content` - Suppression réussie
- `400 Bad Request` - Requête invalide
- `404 Not Found` - Ressource non trouvée
- `500 Internal Server Error` - Erreur serveur

## Exemples d'utilisation avec cURL

### Scénario complet: Créer client → Créer facture → Payer

```bash
# 1. Créer un client
CLIENT_ID=$(curl -s -X POST http://localhost:8085/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Jean Dupont",
    "email": "jean.dupont@example.com",
    "telephone": "0123456789",
    "adresse": "123 Rue Example"
  }' | jq -r '.id')

# 2. Créer une facture
FACTURE_ID=$(curl -s -X POST http://localhost:8085/api/factures \
  -H "Content-Type: application/json" \
  -d "{
    \"clientId\": $CLIENT_ID,
    \"montant\": 1500.00,
    \"dateEmission\": \"2024-01-15\",
    \"dateEcheance\": \"2024-02-15\",
    \"description\": \"Facture de services\"
  }" | jq -r '.id')

# 3. Effectuer un paiement
curl -X POST http://localhost:8085/api/paiements \
  -H "Content-Type: application/json" \
  -d "{
    \"factureId\": $FACTURE_ID,
    \"montant\": 1500.00,
    \"methodePaiement\": \"VIREMENT\"
  }"

# 4. Vérifier le statut de la facture
curl http://localhost:8085/api/factures/$FACTURE_ID

# 5. Vérifier les notifications
curl http://localhost:8085/api/notifications
```

## Notes importantes

1. Toutes les dates sont au format ISO 8601: `YYYY-MM-DD` ou `YYYY-MM-DDTHH:mm:ss`
2. Les montants sont en décimales avec 2 décimales
3. L'API Gateway route automatiquement vers le bon service
4. Les erreurs sont retournées au format JSON avec un champ `error`

