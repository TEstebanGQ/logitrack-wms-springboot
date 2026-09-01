# 🌐 Guía de Despliegue en la Nube — LogiTrack S.A.

Este documento detalla la arquitectura de infraestructura, tecnologías utilizadas y el procedimiento paso a paso para el despliegue de la aplicación **LogiTrack S.A.** en la nube.

---

## 🛠️ Tecnologías e Infraestructura Utilizadas

| Componente | Tecnología / Proveedor | Descripción |
| :--- | :--- | :--- |
| **Servidor Nube (VM)** | **Google Cloud Platform (GCP)** | Instancia Compute Engine `logitrack-server` (`e2-medium`, Ubuntu 22.04 LTS, Región `us-central1` Iowa). |
| **Dirección IP** | **IP Estática Externa GCP** | `34.70.8.165` reservada para garantizar persistencia de red. |
| **Dominio & SSL/TLS** | **Caddy Server v2 + sslip.io** | Reverse Proxy con certificados SSL/TLS automáticos de Let's Encrypt para `https://logitrack.34.70.8.165.sslip.io`. |
| **Contenedorización** | **Docker Compose v2.27.0** | Empaquetado multi-etapa (`eclipse-temurin:17-jre-alpine`) con aislamiento de servicios. |
| **Base de Datos** | **Neon.tech Serverless Postgres** | PostgreSQL 16 alojado en Neon Cloud (con contenedor local PostgreSQL de respaldo). |
| **OAuth2 / Google Auth** | **Google Cloud Console API** | Credencial Client ID OAuth 2.0 configurada para autorizar orígenes web y redirecciones HTTPS. |
| **Servicio de Correo** | **Gmail SMTP** | Autenticación con contraseña de aplicación (`smtp.gmail.com:587`). |

---

## 📐 Arquitectura de Red y Flujo de Tráfico

```text
[ Cliente / Navegador Web ]
           │
           │  HTTPS (Puerto 443) - Certificado SSL Let's Encrypt
           ▼
[ Caddy Reverse Proxy (/etc/caddy/Caddyfile) ]
           │
           │  Proxy Local (http://localhost:8081)
           ▼
[ Contenedor Docker: logitrack_app (Puerto 8081) ]
           │
           │  Spring Boot 3.3 / Java 17
           ▼
[ Base de Datos PostgreSQL / Neon.tech (Puerto 5432 / 5435) ]
```

---

## 📋 Pasos Realizados para el Despliegue en Google Cloud

### 1. Creación de la Instancia en GCP y Reglas de Cortafuegos
- Se creó la máquina virtual `logitrack-server` en GCP Compute Engine.
- Se configuraron las reglas de cortafuegos (*Firewall Rules*) para permitir tráfico de entrada en los puertos `80` (HTTP), `443` (HTTPS) y `8081` (App Backend).

### 2. Instalación de Caddy y Certificados SSL/TLS
Se instaló Caddy Server en la máquina virtual para actuar como Reverse Proxy y gestionar el certificado SSL automáticamente.

Archivo `/etc/caddy/Caddyfile`:
```caddy
logitrack.34.70.8.165.sslip.io, 34.70.8.165.sslip.io {
    reverse_proxy localhost:8081
}
```

### 3. Instalación de Docker y Docker Compose v2
Se instaló el binario oficial de **Docker Compose v2** (`/usr/local/bin/docker-compose` v2.27.0) para resolver la incompatibilidad entre Docker Engine 25+ y el antiguo paquete Python `docker-compose 1.29.2`.

### 4. Configuración de Variables de Entorno (`.env`)
Se creó el archivo `.env` en la raíz del proyecto en el servidor con la siguiente parametrización:

```env
DB_URL=jdbc:postgresql://ep-summer-sun-a41t40j4.us-east-1.aws.neon.tech/neondb?sslmode=require
DB_USERNAME=neondb_owner
DB_PASSWORD=********
JWT_SECRET=LogiTrackSecretKeyWMS2026MasterEnterpriseSuperSecureJWT256BitsKey!
GOOGLE_CLIENT_ID=134514735180-itup9unjaso8pr7lk8vvq9hk0bl81avm.apps.googleusercontent.com
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tomasestebangonzalezquintero@gmail.com
SPRING_MAIL_PASSWORD=aheolaiytleojsul
CORS_ALLOWED_ORIGINS=https://logitrack.34.70.8.165.sslip.io,https://34.70.8.165.sslip.io
PORT=8081
```

---

## 🚀 Comandos de Despliegue y Actualización

Para aplicar actualizaciones de código o reconstruir la aplicación en el servidor de Google Cloud, ejecuta en la terminal SSH:

```bash
# 1. Ingresar al directorio del proyecto
cd ~/Proyecto-Spring-Boot-Tomas-Gonzalez-D1

# 2. Descargar los últimos cambios desde GitHub
git pull

# 3. Re-compilar y reiniciar los contenedores en segundo plano
sudo docker-compose up -d --build
```

---

## 🔍 Comandos Útiles de Monitoreo en el Servidor

- **Ver estado de los contenedores:**
  ```bash
  sudo docker ps
  ```
- **Ver logs de la aplicación en tiempo real:**
  ```bash
  sudo docker-compose logs -f app
  ```
- **Reiniciar el servidor web Caddy:**
  ```bash
  sudo systemctl restart caddy
  ```
