# Usa uma imagem Java 21 (Temurin) como base
FROM eclipse-temurin:21-jdk


# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR gerado para dentro da imagem
COPY target/rest-api-0.0.1-SNAPSHOT.jar app.jar
# COPY target/devradar-mysql-backend.jar app.jar


# Expõe a porta da aplicação
EXPOSE 8080

# Comando para rodar o app
ENTRYPOINT ["java", "-jar", "app.jar"]
