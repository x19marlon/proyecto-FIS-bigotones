# OpenLib Market - Proyecto FIS Bigotones

Este proyecto es una plataforma de mercado de libros que utiliza una arquitectura híbrida con un backend en Spring Boot y un frontend en JavaFX.

## Tecnologías Utilizadas
- **Backend:** Spring Boot, JPA, H2 Database, Maven.
- **Frontend:** JavaFX, SceneBuilder, Java HTTP Client.
- **Pruebas:** JUnit 5, MockMvc.

## Notas / Pendientes

### 1. Datos predeterminados del usuario para facturación
- Cuando un usuario ya exista en el sistema, sus nombres y datos principales deben cargarse automáticamente para el recibo/factura al momento de comprar libros.
- El correo usado para la factura debe ser el correo ya registrado en la cuenta del usuario, no uno ingresado manualmente en el formulario de checkout.

### 2. Validación de direcciones
- Falta implementar una restricción para diferenciar direcciones válidas y no válidas.
- Se debe definir qué reglas se usarán para validar una dirección (ej: formato, existencia, restricciones geográficas) antes de permitir el envío.

### 3. Datos requeridos para envío
- Revisar qué otros datos son necesarios para completar correctamente un envío de libros.
- Determinar si hacen falta campos adicionales como:
  - Ciudad y Departamento/Estado.
  - Código postal.
  - Teléfono de contacto.
  - Dirección secundaria o referencias.

### 4. Descarga de libros
- Falta implementar el feature para descargar libros mediante una URL única tras la compra.
- Esta URL debe ser segura (ej: firmada o temporal), no fácilmente compartible, y estar estrictamente asociada a la compra o al usuario autorizado.

### 5. Consistencia de precios en el UI
- Se ha detectado que en algunas vistas los libros pueden aparecer con costo $0 o como "gratis", pero al llegar al checkout se calcula un costo total.
- Es necesario asegurar que el precio mostrado en el catálogo sea consistente con el valor final cobrado en el pedido.