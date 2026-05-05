# OpenLib Market - Proyecto FIS Bigotones

Este proyecto es una plataforma de mercado de libros que utiliza una arquitectura híbrida con un backend en Spring Boot y un frontend en JavaFX.

## Tecnologías Utilizadas
- **Backend:** Spring Boot, JPA, H2 Database, Maven.
- **Frontend:** JavaFX, SceneBuilder, Java HTTP Client.
- **Pruebas:** JUnit 5, MockMvc.

## Notas pendientes / Funcionalidades por revisar

A continuación se detallan las funcionalidades críticas e inconsistencias identificadas que deben ser abordadas en futuras iteraciones para garantizar un flujo de ecommerce robusto y profesional:

### 1. Autocompletado de datos para el recibo
*   **Estado:** Pendiente de implementación.
*   **Descripción:** El sistema debe autocompletar automáticamente la información del usuario autenticado (nombre, correo electrónico y datos de facturación) al momento de generar el comprobante de compra.
*   **Objetivo:** Mejorar la experiencia de usuario (UX) evitando que deba ingresar manualmente información que la aplicación ya posee.

### 2. Revisión de precios en el flujo de compra
*   **Estado:** En revisión.
*   **Descripción:** Se ha detectado una inconsistencia donde algunos libros aparecen como gratuitos en el catálogo pero generan un total superior a $0 en el checkout.
*   **Objetivo:** Unificar la lógica de precios para que sea coherente en todos los estados: catálogo, carrito, checkout y recibo final.

### 3. Historial de compras
*   **Estado:** Pendiente de diseño y desarrollo.
*   **Descripción:** Implementar una sección dentro del perfil del usuario que permita consultar adquisiciones previas.
*   **Detalles necesarios:** Fecha de transacción, lista de libros, total pagado, estado del pedido y acceso al comprobante digital.

### 4. Descarga de libros
*   **Estado:** Pendiente de implementación técnica.
*   **Descripción:** Habilitar la descarga efectiva de los activos digitales tras la confirmación del pedido.
*   **Seguridad:** Se requiere implementar validación de acceso, enlaces únicos/temporales y protección de la disponibilidad del archivo.

### 5. Coherencia general del flujo de compra
*   **Estado:** En mejora continua.
*   **Descripción:** Asegurar que la trazabilidad del producto sea impecable desde el descubrimiento (catálogo) hasta el consumo (descarga).
*   **Objetivo:** Garantizar que todos los módulos manejen los mismos estados de pago/gratuidad sin discrepancias visuales o lógicas.