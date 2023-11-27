# Start with a base image that has Java 17 installed.
FROM eclipse-temurin:17-jdk-jammy

# Set a default directory inside the container to work from.
WORKDIR /app

# Copy the special Maven files that help us download dependencies.
COPY.mvn/.mvn

# Copy only essential Maven files required to download dependencies.
COPY mvnw pom.xml./

# Download all the required project dependencies.
RUN./mvnw dependency:resolve

# Copy our actual project files (code, resources, etc.) into the container.
COPY src./src

# When the container starts, run the Spring Boot app using Maven.
CMD ["./mvnw", "spring-boot:run"]