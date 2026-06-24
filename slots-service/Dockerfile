FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

COPY pom.xml .

COPY banking-service/pom.xml banking-service/
COPY roulette-service/pom.xml roulette-service/
COPY slots-service/pom.xml slots-service/

COPY slots-service/src slots-service/src

RUN mvn clean package -DskipTests -pl slots-service -am

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/slots-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]