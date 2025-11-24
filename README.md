# GameHub -- Sistema de Gestión de Alquiler de Videojuegos

GameHub es un sistema interno diseñado para optimizar y digitalizar la
gestión de alquileres en una tienda de videojuegos. Permite administrar
clientes, empleados, videojuegos, alquileres y reservas, ofreciendo una
solución centralizada y eficiente.

## Características Principales

### Funcionalidades Implementadas

-   Registro y administración de clientes.
-   Alta, baja y modificación de empleados.
-   Gestión de roles (Founder, Administrador, Empleado).
-   Protección de la cuenta Founder.
-   Registro de alquileres y videojuegos.
-   Control de stock.
-   Seguridad por credenciales y validación de acciones.
-   Registro básico de eventos para auditoría.

### Funcionalidades Pendientes

-   Registro de devoluciones.
-   Penalizaciones por demora o mal estado.
-   Historial de alquileres por cliente.
-   Facturación e impresión de comprobantes.
-   Lista negra y restricciones automáticas.
-   Sistema de reservas completo con notificaciones.
-   Métodos de pago adicionales.

## Arquitectura del Proyecto

### Backend

-   Java\
-   Spring Boot (API REST)\
-   JSON Server (simulación de backend para esta entrega)

### Frontend

-   Angular
-   TypeScript
-   HTML5 / CSS3
-   Angular CLI

### Herramientas

-   GitHub
-   Postman
-   IntelliJ IDEA / VS Code


## 🎯 Objetivo del Sistema

-   Automatización del registro de alquileres.
-   Reducción de errores humanos.
-   Mejor control del stock.
-   Gestión clara de permisos y roles.
-   Base sólida para ampliaciones futuras.

## 📁 Estructura del Proyecto

/gamehub\
├── backend/
├── frontend/
└── README.md

## 🚀 Cómo Ejecutarlo

### 1. Clonar el repositorio

git clone https://github.com/MattM99/TiendaVideojuegos.git
cd frontend

### 2. Iniciar JSON Server

json-server --watch db.json --port 3000

### 3. Iniciar el Frontend

npm install\
ng serve -o

## 👥 Autores

-   Matías Mendoza
-   Kevin Pedro Falcón
-   Nicolás Pettinelli
