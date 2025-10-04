# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-jre-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file to the container
COPY target/trading-app-0.0.1-SNAPSHOT.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]