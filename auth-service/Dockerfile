# Use official JDK image
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app

# Copy Maven wrapper & project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

# Copy source and build jar
COPY src src
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy built jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Create upload directory inside container
RUN mkdir -p /app/uploads

# Expose service port
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
