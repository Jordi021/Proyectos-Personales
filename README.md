# Informe de Aplicación Móvil: Gestor de Proyectos Personales

**Nombre:** `Jordan Puruncajas`
**Fecha:** `19/05/2025`
**Carrera:** `Ingeniería en Software`

## URL del Repositorio GIT

* **URL:** `https://github.com/Jordi021/Proyectos-Personales.git`

## URL del Video Demostrativo

* **URL:** `https://utneduec-my.sharepoint.com/:v:/g/personal/jspuruncajasc_utn_edu_ec/EXM8SeCAFU5Dk87qn39b0mYBgRGIwGMUyvBgp-kn7hTXSw?nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=MofoNy`

## Introducción

La aplicación "Proyectos Personales" ha sido desarrollada como parte de la materia Aplicaciones Móviles. Su objetivo principal es ofrecer a los usuarios una herramienta intuitiva y eficiente para la gestión de sus proyectos personales y las actividades asociadas a estos. La aplicación permite llevar un control detallado desde la creación de un proyecto hasta la finalización de sus tareas, visualizando el progreso en tiempo real. Todas las funcionalidades se basan en el almacenamiento local de datos mediante SQLite, garantizando la disponibilidad de la información sin necesidad de conexión a internet.

## Diseño y Funcionalidad de la Aplicación

La aplicación está estructurada en varios módulos interconectados que facilitan una experiencia de usuario fluida y organizada. A continuación, se describe el funcionamiento de cada pantalla principal:

### 1. Pantalla de Inicio (Carga Inicial)
Al iniciar la aplicación, el usuario es recibido por una pantalla de carga momentánea. Durante este tiempo, el sistema verifica si existe una sesión activa.
* Si el usuario ya ha iniciado sesión previamente, es dirigido automáticamente a la pantalla de "Mis Proyectos".
* En caso contrario, se le presenta la "Pantalla de Inicio de Sesión".

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/1.png" alt="Pantalla de Carga" width="300"/>

### 2. Gestión de Usuarios

#### a. Pantalla de Inicio de Sesión (Login)
Esta pantalla es el punto de acceso a la aplicación.
* El usuario debe ingresar su **nombre de usuario** y **contraseña**.
* Al pulsar "Iniciar Sesión", la aplicación valida estas credenciales contra la información almacenada localmente.
* Si los datos son correctos, el usuario accede a la pantalla de "Mis Proyectos".
* Si los datos son incorrectos, se muestra un mensaje de error.
* Desde aquí, el usuario puede navegar a la pantalla de "Registro" si aún no tiene una cuenta.
* También se ofrece una opción de "Recuperar Contraseña". Al seleccionarla, se solicita al usuario su nombre de usuario y correo electrónico; si coinciden con un registro existente, la aplicación muestra la contraseña almacenada (simulación de recuperación).

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/2.png" alt="Pantalla de Inicio de Sesión" width="300"/>

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/3.png" alt="Diálogo de Recuperar Contraseña" width="300"/>

#### b. Pantalla de Registro de Nuevos Usuarios
Permite a los nuevos usuarios crear una cuenta.
* Se solicitan los siguientes datos: **nombre de usuario**, **correo electrónico** (opcional), **contraseña** y **confirmación de contraseña**.
* La aplicación verifica que el nombre de usuario no esté previamente registrado y que las contraseñas ingresadas coincidan.
* Una vez validados los datos, se crea el nuevo usuario y se almacena su información.
* Tras el registro exitoso, el usuario es redirigido a la "Pantalla de Inicio de Sesión" para que pueda acceder con sus nuevas credenciales.

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/4.png" alt="Pantalla de Registro de Usuarios" width="300"/>

### 3. Módulo de Proyectos

#### a. Pantalla de Listado de Proyectos ("Mis Proyectos")
Es la pantalla principal una vez que el usuario ha iniciado sesión.
* Muestra un saludo personalizado con el nombre del usuario.
* Presenta una lista de todos los proyectos personales creados por el usuario.
* Cada proyecto en la lista muestra su **nombre**, **descripción**, **fechas de inicio y fin**, y una **barra de progreso visual**. Esta barra indica el porcentaje de actividades completadas (en estado "Realizado") para ese proyecto.
* Si el usuario no tiene proyectos creados, se muestra un mensaje indicándolo.
* Un botón flotante (FAB) permite **crear un nuevo proyecto**, llevando al usuario a la pantalla de "Crear/Editar Proyecto".
* Cada proyecto listado cuenta con un menú de opciones que permite:
    * **Ver Actividades:** Navega a la pantalla de "Actividades del Proyecto" correspondiente.
    * **Editar:** Abre la pantalla de "Crear/Editar Proyecto" con los datos del proyecto seleccionado cargados para su modificación.
    * **Eliminar:** Solicita confirmación al usuario y, si se acepta, elimina el proyecto y todas sus actividades asociadas de la base de datos.
* La barra de herramientas superior (Toolbar) incluye una opción para **Cerrar Sesión**, que finaliza la sesión actual y redirige al usuario a la "Pantalla de Inicio de Sesión".

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/6.png" alt="Menú de Opciones del Proyecto" width="300"/>

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/7.png" alt="Pantalla de Crear/Editar Proyecto" width="300"/>

#### b. Pantalla de Crear/Editar Proyecto
Esta pantalla se utiliza tanto para la creación de nuevos proyectos como para la modificación de los existentes.
* Los campos a gestionar son:
    * **Nombre del Proyecto** (obligatorio)
    * **Descripción** (opcional)
    * **Fecha de Inicio** (seleccionable mediante un calendario emergente)
    * **Fecha de Fin** (opcional, seleccionable mediante un calendario emergente)
* Al guardar, la información se almacena o actualiza en la base de datos local. La aplicación valida que la fecha de fin no sea anterior a la fecha de inicio.

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/8.png" alt="Listado de Actividades por Proyecto" width="300"/>

### 4. Módulo de Actividades por Proyecto

#### a. Pantalla de Listado de Actividades del Proyecto
Se accede a esta pantalla al seleccionar "Ver Actividades" en un proyecto específico.
* Muestra el **nombre del proyecto** al que pertenecen las actividades y una **barra de progreso general** del proyecto, calculada en base al porcentaje de actividades en estado "Realizado".
* Lista todas las actividades asociadas a ese proyecto.
* Cada actividad en la lista muestra su **nombre**, **descripción**, **fechas de inicio y fin**, y su **estado actual** ("Planificado", "En ejecución", "Realizado") mediante un distintivo visual (Chip) cuyo color varía según el estado.
* Si no hay actividades para el proyecto, se muestra un mensaje correspondiente.
* Un botón flotante (FAB) permite **registrar una nueva actividad** para el proyecto actual, llevando a la pantalla "Crear/Editar Actividad".
* Al seleccionar una actividad de la lista, se presenta un menú de opciones que permite:
    * **Editar Actividad:** Abre la pantalla "Crear/Editar Actividad" con los datos de la actividad seleccionada.
    * **Eliminar Actividad:** Solicita confirmación y, si se acepta, elimina la actividad.

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/9.png" alt="Menú de Opciones de la Actividad" width="300"/>

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/10.png" alt="Pantalla de Crear/Editar Actividad" width="300"/>

#### b. Pantalla de Crear/Editar Actividad
Permite la creación o modificación de actividades dentro de un proyecto.
* Los campos a gestionar son:
    * **Nombre de la Actividad** (obligatorio)
    * **Descripción** (opcional)
    * **Fecha de Inicio** (seleccionable mediante un calendario emergente)
    * **Fecha de Fin** (opcional, seleccionable mediante un calendario emergente)
    * **Estado:** Seleccionable de una lista desplegable ("Pendiente" como estado por defecto para nuevas actividades, "En Progreso", "Realizado").
* La aplicación valida que la fecha de fin no sea anterior a la de inicio. Al guardar, los datos se almacenan o actualizan.

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/11.png" alt="Descripción de la imagen 11" width="300"/>

## Diseño de la Base de Datos (SQLite)

La persistencia de los datos de la aplicación se gestiona mediante una base de datos SQLite local. El diseño de la base de datos se centra en tres entidades principales: Usuarios, Proyectos y Actividades, con relaciones claramente definidas para mantener la integridad de los datos.

**Esquema de Tablas y Relaciones:**

* **Tabla `usuarios`:** Almacena la información de los usuarios registrados.
    * Campos: `id_usuario` (clave primaria), `nombre_usuario` (único), `contrasena`, `email`.
* **Tabla `proyectos`:** Guarda los detalles de cada proyecto.
    * Campos: `id_proyecto` (clave primaria), `nombre_proyecto`, `descripcion`, `fecha_inicio`, `fecha_fin`, `id_usuario_fk` (clave foránea a `usuarios`).
* **Tabla `actividades`:** Contiene la información de las actividades de cada proyecto.
    * Campos: `id_actividad` (clave primaria), `nombre_actividad`, `descripcion`, `fecha_inicio`, `fecha_fin`, `estado`, `id_proyecto_fk` (clave foránea a `proyectos`).

**Relaciones:**
* Un **usuario** puede tener múltiples **proyectos**.
* Un **proyecto** puede tener múltiples **actividades**.
* La eliminación de un usuario conlleva la eliminación en cascada de sus proyectos y, consecuentemente, de las actividades de dichos proyectos. Similarmente, la eliminación de un proyecto elimina en cascada sus actividades asociadas.

**Diagrama Entidad-Relación (o Esquema Visual):**

<img src="file:///D:/Projects/AndroidStudio/PersonalProjectsGe/docs/images/db.png" alt="Diagrama de Base de Datos" width="500"/>

