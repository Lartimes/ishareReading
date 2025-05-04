FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the built JAR file
COPY ../backend-reading/target/backend-reading-1.0.0.jar app.jar
COPY ../data    data/
# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]