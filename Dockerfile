# ---- Build stage ----
FROM maven:3.9.13-eclipse-temurin-21-alpine AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# ---- Run stage ----
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=builder /build/target/acebook-template-1.0-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]