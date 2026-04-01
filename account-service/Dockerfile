# Use official OpenJDK image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy build jar from Maven
COPY target/account-service-0.0.1-SNAPSHOT.jar account-service.jar

# Expose port
EXPOSE 8082

# Run the jar
ENTRYPOINT ["java", "-jar", "account-service.jar"]
