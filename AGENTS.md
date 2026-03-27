
# 📑 Documentación del Proyecto: Springpoo (SENA)

## 1. Información General
*   **Nombre del Proyecto:** Springpoo
*   **Framework Base:** Spring Boot 4.0.3 (Versión de vanguardia)
*   **Paradigma:** Desarrollo Full-Stack con arquitectura MVC (Modelo-Vista-Controlador) asistida por persistencia manual mediante JDBC.
*   **Propósito:** Sistema de gestión de usuarios (CRUD) para la institución SENA, permitiendo el registro, consulta, actualización y eliminación de aprendices/administradores.

## 2. Stack Tecnológico (Dependencias)
El proyecto utiliza una combinación de herramientas para el manejo de datos, lógica de servidor y renderizado visual:

*   **Spring Web:** Para la creación de los controladores, manejo de rutas (Endpoints) y respuestas REST/HTTP.
*   **Thymeleaf:** Motor de plantillas para renderizar el HTML dinámico desde el servidor.
*   **MySQL Driver (Connector/J):** Controlador necesario para establecer la comunicación entre Java y la base de datos MariaDB/MySQL.
*   **JDBC (Java Database Connectivity):** Utilizado de forma directa para la ejecución de sentencias SQL sin el uso de ORMs (como JPA).

---

## 3. Estructura de Directorios (Encarpetado Real)
El proyecto sigue el estándar de Maven, organizado de la siguiente manera:

```text
C:.
└───src
    ├───main
    │   ├───java
    │   │   └───com.sena.springpoo
    │   │       ├───controller    # Controladores de API y Vistas
    │   │       ├───models        # Entidades (Usuario, Comunicados, Formularios)
    │   │       └───persistence   # Conexión JDBC y Consultas SQL
    │   └───resources
    │       ├───static            # RECURSOS ESTÁTICOS
    │       │   └───js            # Lógica de internacionalización (i18n.js, translations.js)
    │       ├───templates         # Vistas dinámicas (Thymeleaf)
    │       │   └───error         # Manejo de estados 404 y 500
    │       └───application.properties
    └───test                      # Pruebas de integración
```

---

## 4. Reglas de Desarrollo y Código (Best Practices)

### 🛠 Manejo de la Base de Datos
1.  **Conexión Manual:** Todas las operaciones pasan por la clase `Conexion.java`. Es mandatorio cerrar los flujos de `PreparedStatement` y `Connection` o asegurar su gestión para evitar fugas de memoria.
2.  **SQL Nativo:** Se debe utilizar `PreparedStatement` para todas las consultas con el fin de prevenir ataques de **SQL Injection**.
3.  **Configuración:** La URL de conexión por defecto es `jdbc:mysql://localhost:3306/sena`.

### 🎮 Controladores (Controllers)
1.  **División de Responsabilidades:**
    *   `ControllerAdmin`: Gestiona las respuestas de tipo API (`@ResponseBody`) y la navegación principal.
    *   `CrudController`: Gestiona las vistas del panel administrativo y el flujo de redirecciones.
2.  **Internacionalización Básica:** Se debe validar el Header `Accept-Language`. Si contiene "es", las respuestas de texto deben ser en español; de lo contrario, por defecto en inglés.

### 🏛 Modelos (Models)
1.  **POJO Estricto:** La clase `Usuario` debe mantener sus atributos privados con sus respectivos Getters, Setters y constructores (Vacío y Cargado).
2.  **Mapeo Manual:** Al no usar JPA, el desarrollador es responsable de mapear cada columna del `ResultSet` de SQL al objeto Java manualmente en la capa de persistencia.

### 🎨 Frontend y Estilos (UI/UX)
1.  **Identidad Visual:**
    *   **Fondo:** Blanco (`#ffffff`).
    *   **Contenedores:** Gris Oscuro (`#2d2d2d`).
    *   **Acciones/Botones:** Verde (`#4CAF50`).
2.  **Thymeleaf:** El uso de `th:each` para tablas y `th:if` para mensajes de error/confirmación es obligatorio para mantener la lógica fuera de las etiquetas HTML puras.

---

## 5. Reglas de Negocio (Backend)
*   **Roles:** Los usuarios creados mediante el método `save` se registran automáticamente con el rol `'ADMIN'` por defecto en la base de datos.
*   **Auditoría:** Todas las inserciones y actualizaciones deben disparar las funciones `NOW()` de SQL para las columnas `fecha_registro` y `ultima_actualizacion`.
*   **Manejo de Errores:**
    *   Si una búsqueda por ID falla, se debe retornar la vista `error/404`.
    *   Si una inserción en la BD falla, se debe retornar la vista `error/500`.

---

## 6. Diccionario de Datos (Tabla Usuario)
| Campo | Tipo | Restricción |
| :--- | :--- | :--- |
| `id_usuario` | INT | Primary Key, Not Null |
| `documento` | BIGINT | Unique, Not Null |
| `tipo_documento` | ENUM | CC, TI |
| `correo_electronico`| VARCHAR(100)| Not Null |
| `rol` | ENUM | ADMIN, USUARIO |

---

> **Nota:** Este documento es la "única fuente de verdad" para el mantenimiento del proyecto. Cualquier cambio en la estructura de la base de datos debe ser reflejado inmediatamente en `PersistenceUsuario.java`.