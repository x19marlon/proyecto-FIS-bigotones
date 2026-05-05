# OpenLib Market - Plataforma de Gestión y Adquisición Bibliográfica

OpenLib Market es una solución integral de ecommerce diseñada para la gestión y distribución de activos digitales y libros académicos. El proyecto combina la robustez de un backend desarrollado en **Spring Boot** con una interfaz de usuario moderna y fluida construida en **JavaFX**, ofreciendo una experiencia de usuario profesional, escalable y eficiente.

---

## 📄 Descripción General

La plataforma permite a los usuarios institucionales explorar un catálogo curado de libros, gestionar sus adquisiciones a través de un carrito de compras y completar flujos de checkout seguros. Está diseñada bajo estándares profesionales de diseño visual (Glassmorphism, paletas HSL coherentes) y una arquitectura de software desacoplada que facilita el mantenimiento y la escalabilidad futura.

---

## 🎯 Objetivos del Proyecto

El desarrollo de OpenLib Market se centra en los siguientes pilares:

*   **Optimización de Acceso:** Facilitar la consulta y adquisición de material académico digital de forma intuitiva.
*   **Experiencia de Ecommerce Real:** Simular un flujo de compra completo, desde el descubrimiento del producto hasta la obtención del comprobante.
*   **Arquitectura Robusta:** Implementar una separación clara entre la lógica de negocio (Backend) y la presentación (Frontend).
*   **Escalabilidad:** Sentar las bases técnicas para manejar grandes volúmenes de datos y usuarios mediante una integración eficiente vía API REST.

---

## 🚀 Funcionalidades Principales

### Funcionalidades Actuales
*   **Catálogo Profesional:** Visualización de libros mediante tarjetas (product-cards) con badges dinámicos y estados.
*   **Gestión de Carrito:** Sistema interactivo para agregar, visualizar y eliminar ítems antes de la compra.
*   **Flujo de Checkout:** Proceso de facturación que integra información de envío y métodos de pago simbólicos.
*   **Panel Administrativo:** Gestión centralizada de inventario (CRUD de libros), usuarios y pedidos.
*   **Diseño Responsivo y UX:** Interfaz pulida con CSS personalizado, micro-animaciones y navegación fluida entre escenas.
*   **Sincronización Backend:** Integración real con servicios REST para persistencia y validación de datos.

### Funcionalidades Pendientes / Roadmap
*   **Autocompletado de Datos:** Carga automática de perfiles de usuario en el checkout para agilizar el proceso.
*   **Historial de Compras:** Sección dedicada para que el usuario consulte sus transacciones y acceda a sus comprobantes.
*   **Descarga Segura:** Implementación de acceso a archivos mediante URLs únicas y temporales.
*   **Filtrado por Categorías:** Corregir la funcionalidad de búsqueda por categorías en el catálogo, la cual se encuentra actualmente inactiva.
*   **Paginación del Lado del Servidor:** Optimización del catálogo para soportar miles de registros sin degradación de rendimiento.
*   **Validación Avanzada de Datos:** Refinamiento de las reglas de negocio para facturación y estados de pedido.

---

## 🛠️ Tecnologías Utilizadas

*   **Lenguaje:** Java 21
*   **Backend:** Spring Boot 3.2.5 (Spring Data JPA, Web, H2 Database)
*   **Frontend:** JavaFX (Layouts dinámicos, SceneManager)
*   **Estilos:** CSS Vanilla (Diseño basado en paleta: Azul Petróleo, Beige Cálido, Verde Salvia)
*   **Gestión de Dependencias:** Maven
*   **Persistencia:** Base de datos H2 en memoria (ideal para desarrollo y pruebas)

---

## 📁 Estructura del Proyecto

```bash
proyecto-FIS-bigotones/
├── src/
│   ├── main/
│   │   ├── java/com/openlib/
│   │   │   ├── controller/      # Lógica de orquestación de UI y llamadas API
│   │   │   ├── model/           # Entidades de datos (Book, User, Order)
│   │   │   ├── repository/      # Interfaces de acceso a datos (Spring Data)
│   │   │   ├── service/         # Lógica de negocio del backend
│   │   │   ├── util/            # Utilidades, configuración de API y navegación
│   │   │   ├── view/            # Componentes y pantallas de la interfaz JavaFX
│   │   │   └── MainApp.java     # Punto de entrada de la aplicación UI
│   │   └── resources/
│   │       ├── application.properties  # Configuración del servidor
│   │       └── css/                    # Hojas de estilo globales
├── DOC/                         # Documentación de historias de usuario y backlog
├── pom.xml                      # Configuración de Maven y dependencias
└── README.md                    # Documentación principal
```

---

## ⚙️ Instrucciones de Ejecución

Para ejecutar el proyecto correctamente, se recomienda seguir este orden:

### 1. Iniciar el Servidor (Backend)
Desde la raíz del proyecto, abre una terminal y ejecuta:
```bash
mvn spring-boot:run
```
*El servidor iniciará por defecto en `http://localhost:8080`.*

### 2. Iniciar la Interfaz (Frontend)
En una terminal diferente, ejecuta:
```bash
mvn javafx:run
```

---

## 👥 Equipo de Desarrollo
**Proyecto FIS - Grupo Bigotones**
Desarrollado con enfoque en calidad académica y buenas prácticas de ingeniería de software.