# Firebase API Test - Sistema de Gestión de Cursos

Aplicación móvil desarrollada para la plataforma Android en lenguaje Java. Proporciona una interfaz para la administración de cursos mediante un sistema CRUD (Crear, Leer, Actualizar y Eliminar), comunicándose directamente con la API REST de Firebase Realtime Database.

---

## Descripción General

El sistema permite gestionar un catálogo de cursos académicos. Realiza peticiones HTTP asíncronas para interactuar con la base de datos en tiempo real de Firebase sin necesidad de utilizar el SDK nativo, empleando la interfaz REST proporcionada por la plataforma.

---

## Funcionalidades del Sistema

* **Registro de Cursos**: Determinación automática de identificadores incrementales a partir de la estructura de datos existente y posterior almacenamiento del registro (`nombreCurso` y `grado`).
* **Búsqueda por Identificador**: Consulta de registros específicos por ID para su inspección o posterior edición en el formulario.
* **Consulta General**: Listado completo de los registros almacenados en la base de datos con formato estructurado.
* **Actualización de Registros**: Modificación de información existente mediante peticiones HTTP con método `PUT`.
* **Eliminación de Registros**: Remoción de datos específicos mediante peticiones HTTP con método `DELETE`.
* **Gestión de Formulario**: Limpieza de los campos de entrada y control de estado de los controles de edición de la interfaz.

---

## Especificaciones Técnicas

* **Lenguaje de Programación**: Java 11
* **Entorno de Desarrollo**: Android SDK (SDK Mínimo 26 / SDK Objetivo 37)
* **Interfaz de Usuario**: Arquitectura basada en XML Layouts (`ConstraintLayout`, componentes Material Design)
* **Comunicación de Red**: Peticiones HTTP asíncronas con `HttpURLConnection` manejadas a través de `ExecutorService`
* **Base de Datos / Backend**: Firebase Realtime Database (API REST)
* **Procesamiento de Datos**: Manipulación y parseo de objetos JSON mediante la API nativa de Android (`JSONObject`, `JSONArray`)

---

## Estructura del Proyecto

```text
app/src/main/
├── java/com/example/firebaseapitest/
│   └── MainActivity.java          # Controlador de la interfaz y lógica de red
└── res/
    ├── layout/
    │   └── activity_main.xml      # Definición de la interfaz de usuario
    └── drawable/
        └── bg_input_rounded.xml    # Estilos gráficos de los componentes de entrada
```

---

## Especificación de la API REST

Interacción con el nodo `cursos` en Firebase Realtime Database.

**URL Base**: `https://apptestapi-3d114-default-rtdb.firebaseio.com/`

| Operación | Método HTTP | Ruta de Endpoint | Descripción |
| :--- | :--- | :--- | :--- |
| Consulta General | `GET` | `/cursos.json` | Recupera la totalidad de los cursos registrados. |
| Consulta por ID | `GET` | `/cursos/{id}.json` | Recupera la información del curso correspondiente al ID indicado. |
| Registro / Modificación | `PUT` | `/cursos/{id}.json` | Almacena o actualiza la información en el nodo del ID especificado. |
| Eliminación | `DELETE` | `/cursos/{id}.json` | Elimina la entidad correspondiente al ID especificado. |

---

## Estructura de Datos JSON

```json
{
  "cursos": {
    "1": {
      "nombreCurso": "Matemáticas",
      "grado": "5to Secundaria"
    },
    "2": {
      "nombreCurso": "Historia",
      "grado": "4to Secundaria"
    }
  }
}
```

---

## Requisitos de Instalación y Ejecución

1. Clonar el repositorio en el equipo local.
2. Abrir el proyecto en Android Studio.
3. Realizar la sincronización de dependencias Gradle.
4. Desplegar la aplicación en un dispositivo físico o emulador con Android 8.0 (API nivel 26) o superior y acceso a Internet.
