# Etapa 1: Compilação com Maven
FROM maven:3.9.6-eclipse-temurin-21 as builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

VOLUME /tmp
EXPOSE 8080

COPY --from=builder /app/target/challenge-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
