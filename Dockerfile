FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

MAINTAINER kg

COPY target/personalBudget-0.0.1-SNAPSHOT.jar personalBudget-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","personalBudget-0.0.1-SNAPSHOT.jar"]