# Step 1: Use an official OpenJDK base image from Docker Hub
FROM eclipse-temurin:21-jdk-alpine

# Step 2: Set the working directory inside the container
WORKDIR /app

# Copy jar
COPY target/acebook-template-1.0-SNAPSHOT.jar app.jar

# Render provides the port via environment variable
ENV PORT=8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]