# Use a imagem oficial do OpenJDK como imagem base
FROM openjdk:11-jre-slim

# Defina o diretório de trabalho
WORKDIR /app

# Copie o arquivo JAR da aplicação para o contêiner
COPY target/ApiDocumentosEletronicoOracle.jar /app/ApiDocumentosEletronicoOracle.jar

# Expõe a porta que a aplicação Java está ouvindo (se necessário)
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "ApiDocumentosEletronicoOracle.jar"]