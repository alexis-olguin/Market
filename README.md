# Proyecto Minimarket — Arquitectura de Microservicios

Este proyecto consiste en una plataforma de gestión para un **Minimarket**, diseñada y desarrollada bajo una arquitectura distribuida basada en microservicios utilizando **Spring Boot** y **Spring Cloud**.

---

## 2. Dominio y Contexto del Proyecto
El sistema gestiona de manera descentralizada las operaciones esenciales de un negocio de Minimarket:
* **Autenticación y Seguridad:** Registro, inicio de sesión y gestión de roles y usuarios mediante tokens **JWT** firmados.
* **Catálogo de Productos:** Control de stock, precios y marcas de productos.
* **Configuración del Negocio:** Gestión de categorías de productos y tasas de impuestos aplicables.
* **Clientes:** Administración del registro y perfiles de los compradores.

---

## 3. Listado de Microservicios

### Infraestructura y Enrutamiento
| Microservicio | Puerto Local | Base de Datos | Descripción |
|---|---|---|---|
| **ms-eureka** | `8761` | _(no aplica)_ | Servidor de Descubrimiento (Service Discovery) de Eureka. |
| **ms-gateway** | `8080` | _(no aplica)_ | API Gateway central para enrutamiento. |

### Servicios de Negocio Principales
| Microservicio | Puerto Local | Base de Datos | Descripción |
|---|---|---|---|
| **ms-auth** | `8084` | `db_auth` | Emisión de JWT, autenticación de usuarios y roles. |
| **ms-configuración** | `8090` | `db_configuracion` | CRUD de Categorías y Tasas de Impuesto. |
| **ms-producto** | `8081` | `db_producto` | CRUD de Productos y comunicación inter-servicio con ms-configuración. |
| **ms-cliente** | `8085` | `db_cliente` | CRUD de Clientes del Minimarket. |

### Otros Servicios del Sistema
| Microservicio | Puerto Local | Base de Datos | Descripción |
|---|---|---|---|
| **ms-inventario** | `8082` | `db_inventario` | Gestión de stock e inventario de productos. |
| **ms-ventas** | `8083` | `db_ventas` | Gestión de ventas y transacciones. |
| **ms-proveedores** | `8086` | `db_proveedores` | Gestión de compras y proveedores del minimarket. |
| **ms-pagos** | `8087` | `db_pagos` | Registro y conciliación de métodos de pago. |
| **ms-informes** | `8088` | `db_informes` | Generación de reportes de ventas y rendimiento. |
| **ms-notificaciones** | `8089` | `db_notificaciones` | Servicio de alertas y envío de notificaciones. |

---

## 4. Rutas Principales del API Gateway (Puerto 8080)
El cliente consume todos los recursos a través del Gateway:

* **Público (Autenticación):**
  * `POST http://localhost:8080/api/auth/login` → Inicio de sesión (`ms-auth`).
  * `POST http://localhost:8080/api/users` → Crear usuarios (`ms-auth`).
* **Protegido (Negocio):**
  * `/api/categories` & `/api/taxes` → Categorías e impuestos (`ms-configuración`).
  * `/api/productos` → Catálogo de productos (`ms-producto`).
  * `/api/customers` → Administración de clientes (`ms-cliente`).
  * `/api/inventario` → Gestión de inventario (`ms-inventario`).
  * `/api/sales` → Ventas y facturación (`ms-ventas`).
  * `/api/suppliers` & `/api/purchase-orders` → Compras y proveedores (`ms-proveedores`).
  * `/api/payments` → Conciliación de pagos (`ms-pagos`).
  * `/api/reports` → Informes y estadísticas (`ms-informes`).
  * `/api/notificaciones` → Envío de alertas (`ms-notificaciones`).

---

## 5. Documentación de API (Swagger / OpenAPI)
La documentación interactiva se puede consultar directamente en el puerto local de cada servicio:

* **ms-auth:** `http://localhost:8084/swagger-ui.html`
* **ms-configuración:** `http://localhost:8090/swagger-ui.html`
* **ms-producto:** `http://localhost:8081/swagger-ui.html`
* **ms-cliente:** `http://localhost:8085/swagger-ui.html`
* **ms-inventario:** `http://localhost:8082/swagger-ui.html`
* **ms-ventas:** `http://localhost:8083/swagger-ui.html`
* **ms-proveedores:** `http://localhost:8086/swagger-ui.html`
* **ms-pagos:** `http://localhost:8087/swagger-ui.html`
* **ms-informes:** `http://localhost:8088/swagger-ui.html`
* **ms-notificaciones:** `http://localhost:8089/swagger-ui.html`

---

## 6. Instrucciones de Ejecución

### A. Compilación Obligatoria (Antes de usar Docker)
Dado que los archivos compilados `.jar` están en la carpeta `target/` (la cual está excluida de Git en el archivo `.gitignore`), **debes compilar los microservicios** antes de poder construir sus imágenes Docker. 

Hemos provisto un script automatizado en la raíz para facilitar esto en Windows:
* **En Windows (CMD/PowerShell):** Haz doble click o ejecuta `build_all.bat` en tu terminal.

*(En macOS/Linux, puedes ejecutar `./mvnw clean package -DskipTests` en la carpeta de cada microservicio activo).*

El script `build_all.bat` compilará recursivamente los 6 microservicios activos y generará sus respectivos archivos `.jar`.

---

### B. Ejecución con Docker Compose
Una vez finalizada la compilación, puedes levantar todo el ecosistema (los 6 microservicios y sus respectivas 4 bases de datos MySQL) con un solo comando:
```bash
docker compose up --build
```

> [!WARNING]
> **Resolución de Errores Comunes al Levantar Docker:**
> 1. **Error: `COPY failed: no source files were specified`:** Esto ocurre si intentas levantar Docker sin haber ejecutado la compilación previa (`build_all.bat`).
> 2. **Error: `port already in use (8080 o 8761)`:** Si el puerto `8080` (Gateway) o `8761` (Eureka) está ocupado por otra aplicación local (como Tomcat, Jenkins, Oracle XE, etc.), Docker fallará. Debes detener el servicio que ocupa ese puerto en tu máquina antes de levantar el docker-compose.
> 3. **Error: `port already in use (3307 al 3310)`:** Si tienes contenedores MySQL previos corriendo en los mismos puertos asignados para el host, detenlos primero con `docker stop $(docker ps -aq)` o cambia los puertos en `docker-compose.yml`.

---

### C. Ejecución Local (desde el IDE sin Docker)
Si deseas levantar los servicios manualmente desde IntelliJ IDEA o VS Code:
1. Levanta el servidor de descubrimiento **ms-eureka** (Puerto 8761).
2. Arranca las bases de datos de forma local en tu máquina.
3. Configura las variables de entorno de base de datos en tu IDE.
4. Arranca los microservicios de negocio (`ms-auth`, `ms-configuración`, `ms-producto`, `ms-cliente`).
5. Levanta el API Gateway **ms-gateway** (Puerto 8080).

---

### D. Ejecución Remota (Despliegue Cloud en Railway / Render)
1. Sube este repositorio monorepo a tu cuenta de GitHub.
2. En la plataforma PaaS, crea un servicio web por cada microservicio apuntando al directorio raíz de cada uno (Root Directory).
3. Levanta las bases de datos MySQL requeridas e inyecta sus credenciales mediante variables de entorno (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`).
4. Inyecta la variable `EUREKA_URL` en todos los servicios apuntando a la URL pública de tu Eureka en la nube:
   `EUREKA_URL=https://tu-eureka-cloud.up.railway.app/eureka/`
5. Asegúrate de configurar el mismo `JWT_SECRET` en todos los servicios que requieran autenticación.

