# 🛒 Minimarket — Arquitectura de Microservicios

Sistema de gestión de minimarket construido con **Spring Boot 3.4.1** y **Spring Cloud 2024.0.0**.

---

## ⚙️ Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Java JDK | 17 |
| Git | cualquiera |
| Docker Desktop | cualquiera |

> Sin Docker, necesitas MySQL 8 instalado y crear las bases de datos manualmente.

---

## 🚀 Levantar el proyecto (con Docker)

```bash
# 1. Clonar el repositorio
git clone https://github.com/alexis-olguin/Market.git
cd Market

# 2. Levantar todo (bases de datos + microservicios)
docker-compose up --build
```

Primera ejecución: Maven descarga dependencias (~300 MB). Espera a que todos los contenedores digan `Started`.

---

## 🔢 Orden de inicio (sin Docker)

Si levantas los microservicios manualmente desde VS Code o terminal, respeta este orden:

```
1. ms-eureka        → http://localhost:8761
2. ms-auth          → http://localhost:8084
3. ms-configuración → http://localhost:8090
4. ms-gateway       → http://localhost:8080
5. (resto en cualquier orden)
```

Comando por microservicio:
```bash
cd ms-eureka
.\mvnw spring-boot:run
```

---

## 🏗️ Microservicios

| Servicio | Puerto | Descripción |
|---|---|---|
| ms-eureka | 8761 | Service Discovery |
| ms-gateway | 8080 | API Gateway (entrada única) |
| ms-auth | 8084 | Autenticación JWT |
| ms-configuración | 8090 | Impuestos y categorías |
| ms-producto | 8081 | Catálogo de productos |
| ms-cliente | 8085 | Gestión de clientes |
| ms-ventas | 8083 | Procesamiento de ventas |
| ms-inventario | 8086 | Control de stock |
| ms-pagos | 8091 | Pagos y transacciones |
| ms-proveedores | 8087 | Órdenes de compra |
| ms-notificaciones | 8089 | Alertas y notificaciones |
| ms-informes | 8088 | Reportes de negocio |

---

## 🧪 Ejecutar pruebas unitarias

```bash
# Desde la carpeta de cualquier microservicio
cd ms-cliente
.\mvnw clean test
```

---

## 🔑 Credenciales por defecto

| Parámetro | Valor |
|---|---|
| DB usuario | `root` |
| DB contraseña | *(vacía)* |
| JWT Secret | `clave_super_secreta_12345678901234562026` |

Los valores se pueden sobreescribir con variables de entorno: `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`.

---

## 📄 Documentación API (Swagger)

Disponible en cada microservicio:
```
http://localhost:{puerto}/swagger-ui.html
```
