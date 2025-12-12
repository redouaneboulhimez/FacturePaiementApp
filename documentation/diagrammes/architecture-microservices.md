# Diagramme d'Architecture Microservices

## Vue d'ensemble

```
┌─────────────────────────────────────────────────────────────────┐
│                         Angular Frontend                        │
│                      (Port 4200 - à créer)                     │
└────────────────────────────┬──────────────────────────────────┘
                              │
                              │ HTTP/REST
                              │
┌─────────────────────────────▼──────────────────────────────────┐
│                    Spring Cloud Gateway                        │
│                      (Port 8080)                               │
│  - Routing                                                    │
│  - Load Balancing                                             │
│  - Rate Limiting (optionnel)                                  │
└───┬──────────┬──────────┬──────────┬──────────┬──────────────┘
    │          │          │          │          │
    │          │          │          │          │
┌───▼───┐ ┌───▼───┐ ┌───▼───┐ ┌───▼───┐ ┌───▼──────────────┐
│Client │ │Facture│ │Paiement│ │Notif. │ │  Eureka Server   │
│Service│ │Service│ │Service │ │Service│ │   (Port 8761)    │
│:8081  │ │:8082  │ │:8083  │ │:8084  │ │                  │
└───┬───┘ └───┬───┘ └───┬───┘ └───┬───┘ └──────────────────┘
    │         │         │         │              │
    │         │         │         │              │
    │         │         │         │              │
┌───▼───┐ ┌───▼───┐ ┌───▼───┐ ┌───▼───┐         │
│client │ │facture│ │paiement│ │notif. │         │
│_db    │ │_db    │ │_db    │ │_db    │         │
└───────┘ └───────┘ └───────┘ └───────┘         │
                                                 │
                                    ┌────────────┘
                                    │
                         ┌──────────▼──────────┐
                         │  Config Server      │
                         │   (Port 8888)       │
                         └─────────────────────┘

Communication:
- Synchrone: OpenFeign (Paiement → Facture, Paiement → Client)
- Asynchrone: Kafka (Paiement → Notification, Batch → Notification)
```

## Flux de communication

### 1. Communication synchrone (OpenFeign)

```
Paiement Service
    │
    ├─► Facture Service (GET /factures/{id})
    │   └─► Retourne les détails de la facture
    │
    └─► Client Service (GET /clients/{id})
        └─► Retourne les détails du client
```

### 2. Communication asynchrone (Kafka)

```
Paiement Service
    │
    └─► Kafka Topic: "paiement-topic"
        │
        └─► Notification Service (Listener)
            └─► Envoie email/SMS

Facture Service (Batch)
    │
    └─► Kafka Topic: "relance-topic"
        │
        └─► Notification Service (Listener)
            └─► Envoie email de relance
```

## Bases de données (Database per Service)

```
┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│ client_db   │  │ facture_db  │  │ paiement_db │  │notification │
│             │  │             │  │             │  │    _db      │
│ - clients   │  │ - factures  │  │ - paiements │  │ -notifications│
└─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘
```

## Technologies par composant

| Composant | Technologies |
|-----------|-------------|
| Frontend | Angular, TypeScript, RxJS |
| API Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka |
| Config | Spring Cloud Config |
| Communication Synchrone | OpenFeign |
| Communication Asynchrone | Apache Kafka |
| Base de données | MySQL |
| Batch | Spring Batch |
| Monitoring | Spring Boot Actuator |

