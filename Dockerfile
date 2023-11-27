# Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim as build

# Add Maintainer Info
LABEL maintainer="your.email@example.com"

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle Wrapper and build.gradle files to the container
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy the project source
COPY src src

# Grant execution permissions to the Gradle Wrapper
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew clean build -x test

# Start with a new, clean image for runtime
FROM openjdk:17-jre-slim

# Copy the built artifact from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]
