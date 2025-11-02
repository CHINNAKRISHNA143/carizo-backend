# Step 1: Use Java 17 image
FROM openjdk:17-jdk-slim

# Step 2: Set working directory
WORKDIR /app

# Step 3: Copy the jar file from your target folder
COPY target/*.jar app.jar

# Step 4: Expose the port (8080 is Spring Boot default)
EXPOSE 8080

# Step 5: Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
