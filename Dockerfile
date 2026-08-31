# ==========================================
# Etapa 1: Descarga y caché de dependencias
# ==========================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS deps
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

# ==========================================
# Etapa 2: Stage de Tests Unitarios / Integración [H-017]
# ==========================================
FROM deps AS tester
WORKDIR /app
COPY src ./src
RUN mvn test -B

# ==========================================
# Etapa 3: Stage de Compilación y Build [H-017]
# ==========================================
FROM deps AS builder
WORKDIR /app
COPY src ./src
RUN mvn package -DskipTests -B

# ==========================================
# Etapa 4: Imagen final liviana de ejecución (JRE)
# ==========================================
FROM eclipse-temurin:17-jre-alpine AS runner

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

