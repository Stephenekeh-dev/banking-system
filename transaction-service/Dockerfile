# Use JDK 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven/Gradle wrapper and source
COPY . .

# Package the application (assuming Maven)
RUN ./mvnw clean package -DskipTests

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "target/*.jar"]
