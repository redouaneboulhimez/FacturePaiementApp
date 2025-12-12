@echo off
REM Script de configuration de l'environnement pour Windows
REM Usage: setup-env.bat

echo Configuration de l'environnement...

REM Définir JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-21
echo JAVA_HOME defini: %JAVA_HOME%

REM Ajouter Java au PATH
set PATH=%JAVA_HOME%\bin;%PATH%

REM Ajouter Maven au PATH (IntelliJ IDEA)
set MAVEN_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.1.1.1\plugins\maven\lib\maven3
set PATH=%MAVEN_HOME%\bin;%PATH%

echo.
echo Verification:
java -version
echo.
mvn -version
echo.
echo Environnement configure! Vous pouvez maintenant utiliser mvn et java.
echo.
echo Note: Ces variables sont definies pour cette session seulement.
echo Pour les rendre permanentes, ajoutez-les dans les Variables d'environnement Windows.

