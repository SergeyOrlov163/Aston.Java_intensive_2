FROM openjdk:22-jdk-slim
COPY target/gateway-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]