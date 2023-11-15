FROM openjdk:17-jdk-alpine

RUN mkdir /app

COPY FeedAppApplication.jar /app/app.jar

WORKDIR /app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
