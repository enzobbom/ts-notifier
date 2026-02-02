# Basic Docker file
#FROM eclipse-temurin:17-jdk-jammy
#WORKDIR /app
#COPY build/libs/notifier-0.0.1-SNAPSHOT.jar /app/notifier.jar
#EXPOSE 8082
#CMD ["java", "-jar", "/app/notifier.jar"]

# Build
FROM gradle:jdk17 AS build
WORKDIR /app
COPY . .

# Run .jar
RUN gradle build --no-daemon
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/notifier.jar
EXPOSE 8082
CMD ["java", "-jar", "/app/notifier.jar"]

