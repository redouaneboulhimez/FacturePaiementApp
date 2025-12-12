# Diagramme Batch - Relance Automatique des Factures Impayées

## Vue d'ensemble du processus batch

```
┌─────────────────────────────────────────────────────────────┐
│              Spring Batch Scheduler                          │
│         (Exécution quotidienne à minuit)                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       │ Trigger
                       │
┌──────────────────────▼──────────────────────────────────────┐
│              RelanceBatchJob                                 │
│                                                              │
│  Step 1: Reader                                             │
│  ┌────────────────────────────────────────────┐            │
│  │ findByStatutAndDateEcheanceBefore()        │            │
│  │ - Statut: EN_ATTENTE                       │            │
│  │ - DateEcheance < Aujourd'hui               │            │
│  └────────────────────────────────────────────┘            │
│                       │                                      │
│                       ▼                                      │
│  Step 2: Processor                                          │
│  ┌────────────────────────────────────────────┐            │
│  │ Pour chaque facture:                       │            │
│  │ 1. Changer statut → EN_RETARD              │            │
│  │ 2. Préparer message Kafka                  │            │
│  │ 3. Envoyer message Kafka                   │            │
│  └────────────────────────────────────────────┘            │
│                       │                                      │
│                       ▼                                      │
│  Step 3: Writer                                             │
│  ┌────────────────────────────────────────────┐            │
│  │ Sauvegarder factures mises à jour          │            │
│  └────────────────────────────────────────────┘            │
└─────────────────────────────────────────────────────────────┘
                       │
                       │ Kafka Message
                       │
┌──────────────────────▼──────────────────────────────────────┐
│              Kafka Topic: "relance-topic"                   │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       │ Consume
                       │
┌──────────────────────▼──────────────────────────────────────┐
│         Notification Service (Listener)                     │
│                                                              │
│  1. Recevoir message                                        │
│  2. Récupérer email client (via Feign si nécessaire)       │
│  3. Envoyer email de relance                                │
│  4. Sauvegarder notification                                │
└─────────────────────────────────────────────────────────────┘
```

## Séquence détaillée

```
Scheduler          Batch Job         Facture DB      Kafka        Notification Service
   │                   │                  │            │                  │
   │ Cron: 0 0 0 * * ? │                  │            │                  │
   │ (Minuit)          │                  │            │                  │
   ├──────────────────►│                  │            │                  │
   │                   │                  │            │                  │
   │                   │ Reader           │            │                  │
   │                   ├─────────────────►│            │                  │
   │                   │                  │            │                  │
   │                   │◄─────────────────┤            │                  │
   │                   │ Liste factures   │            │                  │
   │                   │ impayées         │            │                  │
   │                   │                  │            │                  │
   │                   │ Processor        │            │                  │
   │                   │ Pour chaque facture:          │                  │
   │                   │                  │            │                  │
   │                   │ 1. Changer statut│            │                  │
   │                   │    EN_RETARD     │            │                  │
   │                   │                  │            │                  │
   │                   │ 2. Send Kafka   │            │                  │
   │                   ├──────────────────────────────►│                  │
   │                   │                  │            │                  │
   │                   │ Writer           │            │                  │
   │                   ├─────────────────►│            │                  │
   │                   │ Save factures    │            │                  │
   │                   │                  │            │                  │
   │                   │                  │            │                  │
   │                   │                  │            │ Consume          │
   │                   │                  │            ├─────────────────►│
   │                   │                  │            │                  │
   │                   │                  │            │                  │ Process
   │                   │                  │            │                  │ 1. Get client email
   │                   │                  │            │                  │ 2. Send email
   │                   │                  │            │                  │ 3. Save notification
   │                   │                  │            │                  │
```

## Configuration du Batch

### Schedule
- **Fréquence**: Tous les jours à minuit (00:00)
- **Cron Expression**: `0 0 0 * * ?`

### Critères de sélection
- Statut: `EN_ATTENTE`
- Date d'échéance: `< Date actuelle`

### Actions effectuées
1. Mise à jour du statut → `EN_RETARD`
2. Envoi d'un message Kafka avec les détails
3. Sauvegarde des modifications

### Gestion des erreurs
- Les erreurs sont loggées
- Le batch continue avec les autres factures
- Les notifications d'échec sont sauvegardées

## Exemple de message Kafka

```json
{
  "factureId": 5,
  "clientId": 2,
  "montant": 1500.00,
  "dateEcheance": "2024-01-10",
  "type": "RELANCE"
}
```

## Métriques et monitoring

- Nombre de factures traitées
- Nombre de notifications envoyées
- Taux de succès/échec
- Temps d'exécution

Ces métriques peuvent être suivies via Spring Boot Actuator.

