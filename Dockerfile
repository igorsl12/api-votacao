# Estágio de construção (Compila o código)
FROM maven:3.8.5-openjdk-25 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Estágio de execução (Roda o servidor)
FROM openjdk:25-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]