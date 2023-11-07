# Utiliser une image de base qui prend en charge le JDK Corretto 20
FROM amazoncorretto:20

RUN mkdir /app

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le jar de votre application Spring Boot dans le conteneur
COPY seasonsforce-ms-invoice-api-1.0-SNAPSHOT.jar /app/app.jar

# Exposer le port sur lequel votre application Spring Boot s'exécute (par défaut : 8080)
EXPOSE 8080

# Commande pour démarrer votre application Spring Boot
CMD ["java", "-jar", "app.jar"]