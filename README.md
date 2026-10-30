# Sistema de Gestión de Contactos (Diseño y Construcción de Interfaces Gráficas)

**Proyecto de la Unidad 2 que implementa un sistema de gestión de contactos con Java, Maven, interfaz gráfica en Java Swing, soporte multiidioma y persistencia local.**

El desarrollo aplica el patrón de arquitectura **MVC (Modelo-Vista-Controlador)** y utiliza `ResourceBundle` para la internacionalización de la interfaz (Español, Inglés y Portugués).

---

### ⚙️ Características Técnicas

* **Arquitectura MVC:** separación entre la interfaz gráfica (`vista`), la lógica de eventos y control (`controlador`), y las entidades con persistencia de datos (`modelo`).
* **Internacionalización:** soporte para Español, Inglés y Portugués mediante `ResourceBundle`. Al cambiar de idioma se actualizan botones, etiquetas, encabezados de tabla, categorías, mensajes y el reporte estadístico, con números y fechas formateados según el idioma.
* **Gestión CRUD:** creación, lectura, actualización y eliminación de contactos, con validaciones de nombre, teléfono, correo y categoría.
* **Interfaz Swing:** `JTable` con ordenamiento por columnas y filtrado por nombre en tiempo real, menú contextual (`JPopupMenu`), atajos de teclado mediante `InputMap`/`ActionMap` y barra de progreso (`JProgressBar`) en la carga de datos.
* **Persistencia y estadísticas:** almacenamiento en archivo de texto (`tabla_contactos.csv`) y generación de un reporte estadístico exportable (`reporte_estadisticas.txt`).
* **Codificación UTF-8:** soporte para tildes y caracteres especiales.

---

### 🛠️ Requisitos Previos

* **Java Development Kit (JDK):** versión 21 o superior (el código usa `switch` con flechas y `Locale.of`).
* **Apache Maven**.
* **IntelliJ IDEA** (recomendado, por el soporte del diseñador de formularios Swing).

---

### 📂 Estructura del Proyecto

```text
src/
├── main/
│   ├── java/
│   │   ├── controlador/
│   │   │   ├── ContactosControlador.java    # Validaciones y operaciones CRUD
│   │   │   ├── Iconos.java                  # Carga de iconos y renderizado de categorías
│   │   │   ├── IdiomaControlador.java       # Gestión del selector de idiomas
│   │   │   ├── LogicaVentana.java           # Coordinador principal, eventos y atajos
│   │   │   ├── ReporteEstadisticas.java     # Generación y guardado de reportes
│   │   │   └── TablaContactos.java          # Modelo, ordenamiento y filtro de la tabla
│   │   ├── modelo/
│   │   │   ├── Persona.java             # Entidad del contacto
│   │   │   └── PersonaDAO.java          # Acceso a datos y persistencia CSV
│   │   └── vista/
│   │       ├── ventana.java             # Ventana principal y punto de arranque
│   │       └── base/
│   │           ├── VentanaUI.java       # Componentes generados por IntelliJ
│   │           └── VentanaUI.form       # Diseño visual (IntelliJ GUI Designer)
│   └── resources/
│       ├── files/
│       │   ├── tabla_contactos.csv          # Persistencia de contactos
│       │   ├── reporte_estadisticas.txt     # Reporte generado (español)
│       │   ├── reporte_estadisticas_en.txt  # Reporte generado (inglés)
│       │   └── reporte_estadisticas_pt.txt  # Reporte generado (portugués)
│       ├── images/
│       │   ├── icons/
│       │   │   ├── application/             # Icono de la aplicación
│       │   │   ├── buttons/                 # Iconos de los botones
│       │   │   └── categories/              # Iconos de las categorías
│       │   └── screenshots/                 # Capturas de ejecución y prototipo
│       ├── messages_en.properties           # Textos en Inglés
│       ├── messages_es.properties           # Textos en Español
│       └── messages_pt.properties           # Textos en Portugués
└── test/
```

---

### 🖥️ Capturas de Ejecución

#### 1. Aplicación en Español
![Aplicación en Español](src/main/resources/images/screenshots/Aplicacion%20en%20el%20Idioma%20Espa%C3%B1ol%20.png)

#### 2. Aplicación en Inglés
![Aplicación en Inglés](src/main/resources/images/screenshots/Aplicacion%20en%20el%20Idioma%20Ingl%C3%A9s%20.png)

#### 3. Aplicación en Portugués
![Aplicación en Portugués](src/main/resources/images/screenshots/Aplicacion%20en%20el%20Idioma%20Portugu%C3%A9s%20.png)

---

### 📥 Obtener el Proyecto

```bash
git clone https://github.com/20Martin05/Diseno-y-Construccion-de-Interfaces-Graficas.git
cd Diseno-y-Construccion-de-Interfaces-Graficas
```

También puedes descargar el proyecto en ZIP desde GitHub y descomprimirlo.

---

### 🎨 Diseño y Prototipado

* **Wireframe:** distribución inicial de la tabla, paneles y formulario.
* **Mockup:** diseño visual con paleta accesible, tipografía `Segoe UI` e iconos.
* **Prototipo interactivo:** versión navegable en Figma para validar el flujo y el cambio de idioma.

![Esquema del Proyecto](src/main/resources/images/screenshots/Esquema%20del%20Proyecto.png)

> 🔗 **Proyecto en Figma:** https://www.figma.com/design/QgQmY2vxKg7GL6av5ryJzb/Dise%C3%B1o-y-Construcci%C3%B3n-de-Interfaces-Gr%C3%A1ficas?node-id=0-1&t=0FKjo7OiWc7pVb6z-1

---

### ▶️ Compilar y Ejecutar

1. Abre el proyecto en IntelliJ IDEA seleccionando el `pom.xml` como proyecto Maven.
2. Marca `src/main/resources` como Resources Root (*Mark Directory as → Resources Root*).
3. Ejecuta la clase `vista.ventana` (`src/main/java/vista/ventana.java`).

Desde la terminal:

```bash
mvn clean package
```

---

### 🚀 Instrucciones de Uso

* **Agregar contacto:** completa Nombres, Teléfono y Correo, elige la categoría, marca Favorito si aplica y pulsa **Añadir Contacto**. El registro aparece en la tabla.
* **Modificar contacto:** selecciona una fila en la tabla; sus datos se cargan en el formulario. Edita y pulsa **Modificar**.
* **Eliminar contacto:** selecciona una fila y pulsa **Eliminar** o la tecla `Supr`.
* **Cambiar idioma:** usa el selector de idioma de la interfaz. Los textos de la pantalla cambian al instante; el reporte estadístico se regenera al actualizarlo o al cambiar de idioma.
* **Atajos de teclado:**
  * `Ctrl + G`: agregar contacto.
  * `Ctrl + S`: modificar contacto seleccionado.
  * `Supr / Delete`: eliminar contacto seleccionado.
  * `Ctrl + E`: exportar lista.
  * `Ctrl + F`: buscar en la tabla.
  * `Escape`: deseleccionar la fila actual.

---

### 👤 Autor

* **Desarrollado por:** Martin Rody Rosero Játiva
* **Materia:** Programación de Interfaces Gráficas
* **Universidad:** Universidad Politécnica Salesiana
* **Nivel / Bloque:** Tercer Semestre, Bloque I (Unidad 2)