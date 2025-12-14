@echo off
REM Script de démarrage de tous les services (Windows)
REM Usage: start-all.bat

echo 🚀 Démarrage de l'application Facture Paiement...
echo.

REM Configuration de l'environnement
echo Configuration de l'environnement...
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

REM Ajouter Maven au PATH (IntelliJ IDEA)
set MAVEN_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.1.1.1\plugins\maven\lib\maven3
set PATH=%MAVEN_HOME%\bin;%PATH%

echo JAVA_HOME: %JAVA_HOME%
echo.

REM Vérifier que Java est disponible
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Erreur: Java n'est pas disponible. Veuillez configurer JAVA_HOME.
    pause
    exit /b 1
)

REM Vérifier que Maven est disponible
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Erreur: Maven n'est pas disponible. Veuillez configurer le PATH pour Maven.
    pause
    exit /b 1
)

REM Créer le dossier logs s'il n'existe pas
if not exist logs mkdir logs

REM Démarrer Eureka Server
echo 1. Démarrage d'Eureka Server...
start "Eureka Server" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH% && cd backend\eureka-server && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

REM Démarrer Config Server
echo 2. Démarrage de Config Server...
start "Config Server" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH% && cd backend\config-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Démarrer Client Service
echo 3. Démarrage de Client Service...
start "Client Service" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH% && cd backend\client-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Démarrer Facture Service
echo 4. Démarrage de Facture Service...
start "Facture Service" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH% && cd backend\facture-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Démarrer Paiement Service
echo 5. Démarrage de Paiement Service...
start "Paiement Service" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH% && cd backend\paiement-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Démarrer Notification Service
echo 6. Démarrage de Notification Service...
start "Notification Service" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH% && cd backend\notification-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Démarrer API Gateway
echo 7. Démarrage de API Gateway...
start "API Gateway" cmd /k "set JAVA_HOME=%JAVA_HOME% && set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH% && cd backend\api-gateway && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo.
echo ✅ Tous les services sont démarrés!
echo.
echo 📊 Eureka Dashboard: http://localhost:8761
echo 🌐 API Gateway: http://localhost:8080
echo.
pause

