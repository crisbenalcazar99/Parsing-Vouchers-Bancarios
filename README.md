# Extraccion de Informacion de Comprobantes Bancarios

## Descripción del Proyecto

### Introducción

Este proyecto es un microservicio desarrollado en Java utilizando Hibernate con JPA y una base de datos PostgreSQL. Su objetivo principal es extraer información de comprobantes bancarios que los clientes suben a través del servicio de Document AI de Google Cloud. El microservicio fue diseñado para ser robusto y escalable, incorporando diversas tecnologías y prácticas recomendadas en el desarrollo de software.

### Arquitectura y Tecnologías

1. **Java Spring Boot**: El microservicio fue construido utilizando Spring Boot, que proporciona una base sólida y simplificada para la creación de aplicaciones Java empresariales. Spring Boot facilita la configuración, el despliegue y la escalabilidad de microservicios, asegurando una integración eficiente con otras tecnologías.
2. **Hibernate con JPA**: Para la gestión de la persistencia y el mapeo objeto-relacional (ORM). Hibernate facilita la interacción con la base de datos PostgreSQL mediante JPA (Java Persistence API).
3. **PostgreSQL**: Base de datos relacional donde se almacena toda la información extraída de los comprobantes y los registros de los consumos de los endpoints.
4. **Google Cloud Document AI**: Servicio de inteligencia artificial utilizado para la extracción de datos de los comprobantes bancarios. Un procesador dedicado fue entrenado con más de 500 comprobantes de distintos tipos para garantizar la precisión de las predicciones.
5. **JWT (JSON Web Tokens)**: Implementado para la validación y autenticación de los usuarios al consumir los endpoints expuestos por el microservicio.

### Funcionalidades

1. **Extracción de Información**:
   - Los usuarios suben sus comprobantes bancarios a través del microservicio.
   - El servicio de Document AI de Google Cloud procesa estos comprobantes y extrae la información relevante.
   - Un procesador especializado, entrenado con una amplia variedad de comprobantes, asegura la precisión en la extracción de datos.

2. **Persistencia de Datos**:
   - La información extraída se guarda en una base de datos PostgreSQL.
   - Se lleva un registro detallado de todos los consumos realizados a través de los endpoints del microservicio.

3. **Seguridad**:
   - Se utiliza JWT para la autenticación y autorización de los usuarios.
   - Cada solicitud a los endpoints del microservicio requiere un token JWT válido, asegurando que solo usuarios autenticados puedan acceder a los recursos.

### Endpoints

El microservicio expone varios endpoints para diferentes operaciones:

1. **Subida de Comprobantes**:
   - Permite a los usuarios subir sus comprobantes bancarios para su procesamiento.
   
2. **Consulta de Información**:
   - Permite consultar la información extraída de los comprobantes procesados.

3. **Registro de Consumos**:
   - Proporciona detalles sobre los consumos realizados por cada usuario, facilitando el monitoreo y análisis del uso del servicio.

### Desarrollo y Entrenamiento del Procesador

- **Entrenamiento del Procesador**:
  - Se utilizó Document AI de Google Cloud para entrenar un procesador específico con más de 500 comprobantes bancarios.
  - El entrenamiento incluyó diversos tipos de comprobantes para cubrir un amplio espectro de variaciones y garantizar la exactitud de las predicciones.

- **Procesamiento de Comprobantes**:
  - Una vez entrenado, el procesador puede identificar y extraer datos clave de los comprobantes bancarios subidos por los usuarios.
  - La información extraída incluye, pero no se limita a, datos como montos, fechas, nombres de los beneficiarios, y detalles de transacciones.

### Conclusión

Este microservicio proporciona una solución completa para la gestión y procesamiento de comprobantes bancarios, combinando tecnologías avanzadas de procesamiento de datos y autenticación segura. La integración con Document AI de Google Cloud y el uso de un procesador especializado entrenado con un amplio conjunto de datos garantizan una alta precisión en la extracción de información, mientras que la implementación de JWT asegura que solo usuarios autenticados puedan interactuar con el servicio. La elección de Spring Boot como el framework principal permite una configuración y despliegue eficientes, asegurando la escalabilidad y robustez del microservicio.
