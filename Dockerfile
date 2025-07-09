FROM eclipse-temurin:21-jdk-alpine-3.20 AS base

#Stage1: Build
FROM base AS builder

WORKDIR /home/build

COPY .mvn .mvn
COPY ./mvnw .
COPY pom.xml .

COPY src src
COPY src/main/resources src/main/resources
RUN ./mvnw clean package -DskipTests

#Stage2: Runner
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /home/app

COPY --from=builder /home/build/target/taskflow-0.0.1-SNAPSHOT.jar app.jar

RUN addgroup --system --gid 1001 app
RUN adduser --system --uid 1001 taskflow

USER taskflow

EXPOSE 8080

CMD ["java" , "-jar", "app.jar"]