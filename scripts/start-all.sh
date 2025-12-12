#!/bin/bash

# Script de démarrage de tous les services
# Usage: ./start-all.sh

echo "🚀 Démarrage de l'application Facture Paiement..."
echo ""

# Couleurs pour les messages
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction pour démarrer un service
start_service() {
    local service_name=$1
    local port=$2
    
    echo -e "${YELLOW}Démarrage de $service_name sur le port $port...${NC}"
    cd "backend/$service_name"
    mvn spring-boot:run > "../../logs/$service_name.log" 2>&1 &
    echo $! > "../../logs/$service_name.pid"
    cd ../..
    sleep 5
    echo -e "${GREEN}✓ $service_name démarré${NC}"
    echo ""
}

# Créer le dossier logs s'il n'existe pas
mkdir -p logs

# Ordre de démarrage
echo "1. Démarrage d'Eureka Server..."
start_service "eureka-server" "8761"

echo "2. Attente de l'enregistrement d'Eureka..."
sleep 10

echo "3. Démarrage de Config Server..."
start_service "config-server" "8888"

sleep 5

echo "4. Démarrage de Client Service..."
start_service "client-service" "8081"

sleep 5

echo "5. Démarrage de Facture Service..."
start_service "facture-service" "8082"

sleep 5

echo "6. Démarrage de Paiement Service..."
start_service "paiement-service" "8083"

sleep 5

echo "7. Démarrage de Notification Service..."
start_service "notification-service" "8084"

sleep 5

echo "8. Démarrage de API Gateway..."
start_service "api-gateway" "8080"

echo ""
echo -e "${GREEN}✅ Tous les services sont démarrés!${NC}"
echo ""
echo "📊 Eureka Dashboard: http://localhost:8761"
echo "🌐 API Gateway: http://localhost:8080"
echo ""
echo "Pour arrêter tous les services: ./stop-all.sh"
echo "Pour voir les logs: tail -f logs/<service-name>.log"

