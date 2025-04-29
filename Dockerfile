# Build Stage
FROM gradle:8.10-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon -x test

# Run Stage
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
