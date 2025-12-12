# Diagramme de Séquence - Paiement → Mise à jour → Notification

## Scénario: Effectuer un paiement

```
Angular          Gateway        Paiement      Facture      Client      Kafka      Notification
  │                │              │             │            │           │            │
  │                │              │             │            │           │            │
  │ POST /paiements│              │             │            │           │            │
  ├───────────────►│              │             │            │           │            │
  │                │              │             │            │           │            │
  │                │ POST /paiements           │            │           │            │
  │                ├─────────────►│            │            │           │            │
  │                │              │            │            │           │            │
  │                │              │ GET /factures/{id}      │           │            │
  │                │              ├───────────►│            │           │            │
  │                │              │            │            │           │            │
  │                │              │◄───────────┤            │           │            │
  │                │              │  Facture   │            │           │            │
  │                │              │            │            │           │            │
  │                │              │ GET /clients/{id}       │           │            │
  │                │              ├────────────────────────►│           │            │
  │                │              │            │            │           │            │
  │                │              │◄────────────────────────┤           │            │
  │                │              │  Client    │            │           │            │
  │                │              │            │            │           │            │
  │                │              │ Vérifier montant       │           │            │
  │                │              │            │            │           │            │
  │                │              │ Créer Paiement         │           │            │
  │                │              │            │            │           │            │
  │                │              │ PUT /factures/{id}/status?statut=PAYEE
  │                │              ├───────────►│            │           │            │
  │                │              │            │            │           │            │
  │                │              │◄───────────┤            │           │            │
  │                │              │  Facture   │            │           │            │
  │                │              │  mise à jour           │           │            │
  │                │              │            │            │           │            │
  │                │              │ Send to Kafka          │           │            │
  │                │              ├─────────────────────────────────────►│            │
  │                │              │  Message: {paiementId, factureId, ...}
  │                │              │            │            │           │            │
  │                │              │◄───────────┤            │           │            │
  │                │              │  Paiement  │            │           │            │
  │                │              │  créé      │            │           │            │
  │                │              │            │            │           │            │
  │                │◄─────────────┤            │            │           │            │
  │                │  Response   │            │            │           │            │
  │                │              │            │            │           │            │
  │◄───────────────┤              │            │            │           │            │
  │                │              │            │            │           │            │
  │                │              │            │            │           │            │
  │                │              │            │            │           │            │
  │                │              │            │            │           │            │
  │                │              │            │            │  Consume  │            │
  │                │              │            │            │◄──────────┤            │
  │                │              │            │            │           │            │
  │                │              │            │            │           │  Process   │
  │                │              │            │            │           ├───────────►│
  │                │              │            │            │           │            │
  │                │              │            │            │           │  Send Email│
  │                │              │            │            │           │            │
  │                │              │            │            │           │  Save Notification│
  │                │              │            │            │           │            │
```

## Détails des étapes

1. **Angular** envoie une requête POST à l'API Gateway
2. **Gateway** route vers le **Paiement Service**
3. **Paiement Service** récupère la facture via **Feign Client**
4. **Paiement Service** récupère le client via **Feign Client**
5. **Paiement Service** vérifie que le montant correspond
6. **Paiement Service** crée le paiement en base
7. **Paiement Service** met à jour le statut de la facture via **Feign Client**
8. **Paiement Service** envoie un message Kafka
9. **Notification Service** consomme le message Kafka
10. **Notification Service** envoie l'email et sauvegarde la notification

## Temps de réponse attendu

- Requête HTTP: < 300ms
- Notification envoyée: < 2 secondes après le paiement

