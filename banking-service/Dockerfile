FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Root-POM
COPY pom.xml .

# Alle drei Service-POMs (Root-POM braucht alle Module)
COPY banking-service/pom.xml banking-service/
COPY roulette-service/pom.xml roulette-service/
COPY slots-service/pom.xml slots-service/

# Nur den Source-Code des banking-service
COPY banking-service/src banking-service/src

RUN mvn clean package -DskipTests -pl banking-service -am

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/banking-service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]