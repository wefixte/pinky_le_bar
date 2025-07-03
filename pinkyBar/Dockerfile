# --- Étape 1 : Builder (Compilation de l'application Spring Boot) ---
FROM eclipse-temurin:21-jdk-alpine AS builder

# Installer Maven dans cette étape de compilation
RUN apk add --no-cache maven

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier pom.xml pour télécharger les dépendances en premier (optimisation du cache Docker)
COPY pom.xml .

# Télécharger les dépendances Maven. Si pom.xml ne change pas, cette étape est mise en cache.
RUN mvn dependency:go-offline -B

# Copier tout le code source de l'application
COPY src ./src

# Compiler l'application Spring Boot et générer le fichier .war
# Le fichier .war sera placé dans target/pinkyBar-0.0.1-SNAPSHOT.war
RUN mvn clean install -DskipTests

# --- Étape 2 : Runtime (Exécution de l'application) ---
# Utiliser une image JRE légère pour l'exécution
FROM eclipse-temurin:21-jre-alpine

# Définir le répertoire de travail dans le conteneur final
WORKDIR /app

# Exposer le port sur lequel l'application Spring Boot écoute
EXPOSE 8080

# Copier le fichier .war généré par l'étape 'builder' vers l'image finale
# Le nom du fichier .war doit correspondre à celui généré par Maven (pinkyBar-0.0.1-SNAPSHOT.war)
COPY --from=builder /app/target/pinkyBar-0.0.1-SNAPSHOT.war app.jar

# Commande pour exécuter l'application Java
CMD ["java", "-jar", "app.jar"]