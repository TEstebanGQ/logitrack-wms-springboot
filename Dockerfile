# ==========================================
# Etapa 1: Compilación con Maven y Java 17
# ==========================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copiar el descriptor del proyecto y el código fuente
COPY pom.xml .
COPY src ./src

# Compilar y empaquetar el JAR omitiendo tests
RUN mvn clean package -DskipTests

# ==========================================
# Etapa 2: Imagen final liviana de ejecución (JRE)
# ==========================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear usuario sin privilegios por seguridad
RUN addgroup -S logitrack && adduser -S logitrack -G logitrack
USER logitrack:logitrack

# Copiar únicamente el archivo JAR generado desde la etapa de compilación
COPY --from=builder /app/target/*.jar app.jar

# Puerto por defecto configurado en Spring Boot
EXPOSE 8081

# Parámetros JVM optimizados para contenedores
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
