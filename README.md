# Catalog Service

Microservicio encargado de la gestión de metadatos, inventario y organización del catálogo musical. Escanea un bucket de almacenamiento (MinIO), extrae metadatos de archivos de audio (MP3, FLAC, etc.) y los expone mediante una API REST diferenciada para contenido público y privado.

## 🛠 Tech Stack

*   **Java 21**
*   **Spring Boot 3.4.1**
*   **PostgreSQL** (Persistencia de metadatos)
*   **MinIO** (Object Storage S3-compatible)
*   **Jaudiotagger** (Extracción de tags ID3/Vorbis)
*   **SpringDoc OpenAPI** (Documentación Swagger)

## 🚀 Funcionalidades Principales

1.  **Escaneo Automático**: Recorre recursivamente el bucket de MinIO detectando nuevos archivos.
2.  **Extracción de Metadatos**: Lee Título, Artista, Álbum, Año, Track #, Duración y Género.
3.  **Detección de Carátulas**:
    *   Extrae imágenes incrustadas en el audio.
    *   Detecta archivos *sidecar* (`cover.jpg`, `folder.png`) en la misma carpeta.
4.  **Control de Visibilidad**:
    *   Todo archivo nuevo se marca como `PRIVATE` por defecto.
    *   Endpoints diferenciados para `PUBLIC` (usuarios finales) y `ADMIN` (gestión total).

## ⚙️ Configuración del Entorno

### 1. Infraestructura (Docker)
El proyecto incluye un `docker-compose.yml` que levanta PostgreSQL y MinIO, y crea automáticamente el bucket `music`.

```bash
docker compose -f infrastructure/docker-compose.yml up -d
```

*   **MinIO Console**: http://localhost:9001 (User: `minioadmin`, Pass: `minioadmin`)
*   **Postgres**: Puerto 5432 (User: `postgres`, Pass: `postgres`, DB: `music_catalog`)

### 2. Ejecución del Microservicio
Asegúrate de que la infraestructura esté arriba y ejecuta:

```bash
./mvnw spring-boot:run
```

La aplicación arrancará en el puerto **8080**.

## 📖 API Documentation (Swagger)

Una vez levantado, accede a la documentación interactiva:
👉 **http://localhost:8080/swagger-ui/index.html**

### Endpoints Clave

*   **Escaneo**: `POST /api/scan/start` (Dispara el proceso en background).
*   **Público**: `/api/public/**` (Solo muestra canciones con `visibility: PUBLIC`).
*   **Administración**: `/api/admin/**` (Muestra todo y permite editar visibilidad).
*   **Gestión**: `PATCH /api/admin/songs/{id}/visibility?visibility=PUBLIC` (Publicar canción).

## 📂 Estructura de Datos
La lógica de almacenamiento en MinIO es agnóstica a la estructura de carpetas.
*   **Recomendado**: `Artista/Album/Cancion.flac`
*   **Metadata**: Se priorizan los Tags internos del archivo sobre los nombres de carpeta.

## 📦 Configuración Original (Spring Initializr)

Esta sección describe la configuración base del proyecto tal como sería generada por Spring Initializr.

*   **Project:** Maven
*   **Language:** Java
*   **Spring Boot:** 4.0.1
*   **Group:** pe.bbg.music
*   **Artifact:** catalog
*   **Name:** catalog
*   **Description:** Microservice for music catalog and metadata management
*   **Package name:** pe.bbg.music.catalog
*   **Packaging:** Jar
*   **Java:** 21
*   **Dependencies:**
    *   Spring Boot Actuator (`spring-boot-starter-actuator`)
    *   Spring Data JPA (`spring-boot-starter-data-jpa`)
    *   Spring Web (`spring-boot-starter-webmvc`)
    *   Config Client (`spring-cloud-starter-config`)
    *   Eureka Discovery Client (`spring-cloud-starter-netflix-eureka-client`)
    *   PostgreSQL Driver (`postgresql`)
    *   Lombok (`lombok`)
    *   Spring Boot DevTools (`spring-boot-devtools`)