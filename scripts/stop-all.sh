#!/bin/bash

# Script d'arrêt de tous les services
# Usage: ./stop-all.sh

echo "🛑 Arrêt de tous les services..."

# Liste des services
services=("eureka-server" "config-server" "client-service" "facture-service" "paiement-service" "notification-service" "api-gateway")

for service in "${services[@]}"; do
    if [ -f "logs/$service.pid" ]; then
        pid=$(cat "logs/$service.pid")
        if ps -p $pid > /dev/null 2>&1; then
            echo "Arrêt de $service (PID: $pid)..."
            kill $pid
            rm "logs/$service.pid"
        else
            echo "$service n'est pas en cours d'exécution"
            rm "logs/$service.pid"
        fi
    else
        echo "$service n'a pas de fichier PID"
    fi
done

echo "✅ Tous les services sont arrêtés"

