FROM eclipse-temurin:17-jre

WORKDIR /app
COPY target/app.jar /app/app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
