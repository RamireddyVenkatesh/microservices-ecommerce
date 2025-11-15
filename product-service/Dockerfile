# Use a lightweight JRE image for running
FROM eclipse-temurin:17-jre-focal AS runtime

# Set working directory inside container
WORKDIR /app

# The target folder is where your local 'mvn package' put the JAR.
# The `docker-compose.yml` build context copies the entire service directory,
# so we copy the JAR from the local build's target folder.
# We use a wildcard (*) to capture the actual JAR name (e.g., product-service-0.0.1-SNAPSHOT.jar).
COPY target/*.jar app.jar

# Expose the default port (Port mapping is handled by docker-compose)
EXPOSE 8080

# Command to run the jar
ENTRYPOINT ["java","-jar","app.jar"]