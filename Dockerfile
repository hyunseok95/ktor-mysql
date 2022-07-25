# syntax=docker/dockerfile:1
FROM gradle:7.4.1-jdk11 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle shadowJar --no-daemon

FROM openjdk:11 AS runner
EXPOSE 8000:8000
WORKDIR /app
COPY --chown=java:java keystore.jks ./keystore.jks
COPY --from=builder /app/build/libs/*.jar ./docker-ktor.jar
ENTRYPOINT java -jar ./docker-ktor.jar
VOLUME /app
