@echo off
REM Script de démarrage de tous les services (Windows)
REM Usage: start-all.bat

echo 🚀 Démarrage de l'application Facture Paiement...
echo.

REM Créer le dossier logs s'il n'existe pas
if not exist logs mkdir logs

REM Démarrer Eureka Server
echo 1. Démarrage d'Eureka Server...
start "Eureka Server" cmd /k "cd backend\eureka-server && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

REM Démarrer Config Server
echo 2. Démarrage de Config Server...
start "Config Server" cmd /k "cd backend\config-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Démarrer Client Service
echo 3. Démarrage de Client Service...
start "Client Service" cmd /k "cd backend\client-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Démarrer Facture Service
echo 4. Démarrage de Facture Service...
start "Facture Service" cmd /k "cd backend\facture-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Démarrer Paiement Service
echo 5. Démarrage de Paiement Service...
start "Paiement Service" cmd /k "cd backend\paiement-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Démarrer Notification Service
echo 6. Démarrage de Notification Service...
start "Notification Service" cmd /k "cd backend\notification-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Démarrer API Gateway
echo 7. Démarrage de API Gateway...
start "API Gateway" cmd /k "cd backend\api-gateway && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo.
echo ✅ Tous les services sont démarrés!
echo.
echo 📊 Eureka Dashboard: http://localhost:8761
echo 🌐 API Gateway: http://localhost:8080
echo.
pause

