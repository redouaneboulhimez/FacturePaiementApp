# Frontend Angular - Application de Gestion de Factures et Paiements

## Installation

```bash
cd frontend
npm install
```

## Démarrage

```bash
npm start
```

L'application sera accessible sur http://localhost:4200

## Structure

- `src/app/components/` - Composants Angular
  - `dashboard/` - Tableau de bord avec statistiques
  - `clients/` - Gestion des clients (CRUD)
  - `factures/` - Gestion des factures
  - `paiements/` - Effectuer des paiements
  - `notifications/` - Liste des notifications

- `src/app/services/` - Services Angular
  - `api.service.ts` - Service pour appeler l'API backend

## Configuration

L'URL de l'API est configurée dans `src/app/services/api.service.ts` :

```typescript
const API_URL = 'http://localhost:8085/api';
```

Assurez-vous que l'API Gateway est démarrée sur le port 8085.

## Fonctionnalités

- ✅ Dashboard avec statistiques
- ✅ CRUD Clients
- ✅ Création de factures
- ✅ Paiement de factures
- ✅ Consultation des notifications
- ✅ Interface responsive et moderne

## Build pour production

```bash
npm run build
```

Les fichiers seront générés dans le dossier `dist/`.

