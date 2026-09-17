# Firebase API Test - Sistema de Gestión de Cursos

Aplicación móvil Android desarrollada en Java que permite gestionar cursos mediante operaciones **CRUD** (Crear, Leer, Actualizar y Eliminar) consumiendo directamente la **API REST de Firebase Realtime Database**.

---

## Características Principales

* **Registrar Curso**: Calcula automáticamente un ID incremental analizando los registros existentes y guarda el nuevo curso (`nombreCurso` y `grado`).
* **Buscar por ID**: Consulta un curso específico por su identificador único y llena los campos del formulario para su visualización o modificación.
* **Listar Cursos**: Recupera y muestra todos los registros almacenados en la base de datos en un formato ordenado y legible.
* **Actualizar Curso**: Permite modificar la información (`nombreCurso` y `grado`) de un curso existente mediante peticiones HTTP `PUT`.
* **Eliminar Curso**: Borra el registro seleccionado de la base de datos mediante peticiones HTTP `DELETE`.
* **Limpiar Campos**: Restablece los campos de entrada y deshabilita los botones de edición previa selección.

---

## Tecnologías y Herramientas

* **Lenguaje**: Java 11
* **Plataforma**: Android SDK (Min SDK 26 / Target SDK 37)
* **Diseño UI**: XML Layouts con `ConstraintLayout`, `Material Design` y drawables personalizados.
* **Conectividad de Red**: `HttpURLConnection` ejecutado asíncronamente con `ExecutorService`.
* **Backend / DB**: Firebase Realtime Database (a través de su interfaz REST).
* **Manejo de Datos**: Parsing manual de objetos y arrays JSON (`JSONObject`, `JSONArray`).

---

## Estructura del Proyecto

```text
app/src/main/
├── java/com/example/firebaseapitest/
│   └── MainActivity.java                 # Control de la UI y ejecución de operaciones HTTP
└── res/
    ├── layout/
    │   └── activity_main.xml             # Formulario de entrada, botones de acción y salida de resultados
    └── drawable/
        └── bg_input_rounded.xml          # Estilo personalizado para los campos de entrada
