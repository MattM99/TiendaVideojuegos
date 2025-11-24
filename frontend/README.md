# Descripción general

Este proyecto es un frontend desarrollado íntegramente en [Angular CLI](https://github.com/angular/angular-cli) 20.3.8, que implementa un sistema interno de gestión para una tienda de alquiler de videojuegos.

Para esta entrega académica, el backend real fue reemplazado por JSON Server para simular una API REST local, permitiendo realizar todas las operaciones CRUD necesarias.

El sistema permite administrar clientes, videojuegos, cuentas de usuario, alquileres, devoluciones, penalizaciones y reservas

# Tecnologías utilizadas

## Frontend

  Angular 20

  TypeScript

  Angular CLI

  HTML5 / CSS3

## Angular Material (UI)

  RxJS

  Validaciones con Reactive Forms

## Backend simulado

  JSON Server (simulación de API REST)

  Archivos JSON persistentes para entidades

  Endpoints personalizados con rutas REST

## Herramientas

  Node.js + npm

  GitHub

  VS Code

# Estructura del proyecto
/src
 ├── app
 │   ├── core          → servicios globales, guards, interceptors
 │   ├── shared        → header, footer y frontpage
 │   ├── auth          → login, roles, guards
 │   ├── videojuego   → CRUD de videojuegos
 │   ├── personas      → CRUD de personas (clientes)
 │   ├── cuentas       → CRUD de cuentas/usuarios del sistema
 │   ├── alquileres    → registro + devoluciones + penalizaciones
 │   └── dashboard     → estadísticas con Chart.js
/db.json               → base de datos JSON Server


# Funcionalidades principales
CRUD completos

## Videojuegos:
Crear, editar, eliminar y listar videojuegos, con stock por consola.

## Personas (clientes):
Alta de clientes, historial y edición de datos.

## Cuentas / Usuarios internos:
Registro de empleados y administración de roles (empleado / administrador / founder).

# Autenticación y roles
Implementación del sistema de login completamente en Angular:

  Login con validación de credenciales almacenadas en JSON Server

  Roles:

    Empleado

    Administrador

    Founder (creado manualmente en db.json)

  Persistencia de sesión con LocalStorage

  Guards:

    AuthGuard

    RoleGuard

    GuestGuard

# Diseño y experiencia de usuario
Completamente responsive y basado en Angular Material:

Side nav adaptable

Tablas con paginación y filtros

Tarjetas (cards) para métricas

Formularios con validaciones reactivas

Íconos Material Icons

# Patrones y buenas prácticas incorporadas

Arquitectura modular (feature modules + lazy loading)

Servicios tipados con interfaces

DTOs y modelos separados

Pipes para formateo de datos

Guards para control de acceso

Interceptor para adjuntar “token” simulado

Manejo de errores con HttpErrorResponse

Observables bien manejados con RxJS

# Configuración de JSON Server
  ## Instalación
  npm install -g json-server

  ## Ejecutar backend simulado
  json-server --watch db.json --port 3000
  Esto expone endpoints tales como:
  GET    /videojuegos  
  POST   /videojuegos  
  GET    /personas  
  GET    /cuentas  
  PATCH  /alquileres/1  

  ## Ejecutar Angular
  Instalar dependencias:
    npm install
  Ejecutar el servidor:
    ng serve -o

# Conclusión
Este proyecto demuestra una implementación completa y realista de un sistema de gestión empresarial utilizando Angular como framework principal, con apoyo de JSON Server para simular una API REST.

A pesar de no contar con backend real en esta entrega, el sistema reproduce el flujo real del negocio, respetando principios de arquitectura, usabilidad y mantenibilidad.


