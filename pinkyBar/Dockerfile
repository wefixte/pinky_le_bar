FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
EXPOSE 8080
COPY target/pinkyBar-0.0.1-SNAPSHOT.war app.jar
CMD ["java", "-jar", "app.jar"]