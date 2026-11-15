# Etapa 1: Construccion con Maven y Java 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY gestify-backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY gestify-backend/src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecucion ligera en produccion
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]