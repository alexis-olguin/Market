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

### A. Ejecución Local (desde el IDE o terminal)
1. Levanta el servidor de descubrimiento **ms-eureka**.
2. Arranca los microservicios de negocio que desees probar (ej. **ms-auth**, **ms-producto**, etc.).
3. Levanta el API Gateway **ms-gateway**.
4. *(Opcional)* Si deseas levantar los servicios base y sus bases de datos en contenedores locales, puedes ejecutar:
   ```bash
   docker compose up --build
   ```

### B. Ejecución Remota (Despliegue Cloud en Railway / Render)
1. Sube este repositorio monorepo a tu cuenta de GitHub.
2. En la plataforma PaaS, crea un servicio web por cada microservicio apuntando al directorio raíz de cada uno (Root Directory).
3. Levanta las bases de datos MySQL requeridas e inyecta sus credenciales mediante variables de entorno (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`).
4. Inyecta la variable `EUREKA_URL` en todos los servicios apuntando a la URL pública de tu Eureka en la nube:
   `EUREKA_URL=https://tu-eureka-cloud.up.railway.app/eureka/`
5. Asegúrate de configurar el mismo `JWT_SECRET` en todos los servicios que requieran autenticación.
