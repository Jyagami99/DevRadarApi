#!/bin/bash

# Parar e remover containers existentes
echo "Parando containers antigos..."
docker-compose down

# Compilar o projeto Java com Maven
echo "Compilando o projeto com Maven..."
./mvnw clean package -DskipTests

# Verifica se o JAR foi criado com sucesso
JAR_FILE=target/rest-api-0.0.1-SNAPSHOT.jar

#JAR_FILE=target/devradar-mysql-backend.jar
if [ ! -f "$JAR_FILE" ]; then
  echo "Erro: JAR '$JAR_FILE' não encontrado. Certifique-se de que o nome esteja correto."
  exit 1
fi

# Subir os containers com build
echo "Iniciando os containers com Docker Compose..."
docker-compose up --build
