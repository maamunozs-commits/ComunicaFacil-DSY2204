# ComunicaFácil

**Estudiante:** Matías Andrés Muñoz Soto

**Asignatura:** Desarrollo de Aplicaciones Móviles (DSY2204)

**Sección:** 001A

**Carrera:** Ingeniería en Desarrollo de Software

**Profesor:** Miguel Puebla

**Fecha de entrega:** 23-08-2026

**Repositorio:** https://github.com/maamunozs-commits/ComunicaFacil-DSY2204

## Presentación

Estimado profesor Miguel Puebla:

Junto con saludar, presento **ComunicaFácil**, mi proyecto Android para esta etapa de la asignatura. Desarrollé la aplicación con el propósito de apoyar la comunicación cotidiana de personas con discapacidad sensorial auditiva. Para ello incorporé una interfaz clara, confirmaciones visuales, frases rápidas y una función que reproduce en voz alta los mensajes escritos.

En esta entrega busqué aplicar los contenidos revisados en clases mediante Android Studio, Kotlin, Material Design 3 y Jetpack Compose. El proyecto incluye las vistas de inicio de sesión, registro, recuperación de contraseña y un tablero de comunicación. También integré los controles solicitados y un arreglo local con capacidad para cinco usuarios.

## Funcionalidades implementadas

- Inicio de sesión con validación local.
- Registro con inputs, botones, combo box, check list, radio buttons y tabla resumen.
- Arreglo en memoria con capacidad exacta para cinco usuarios.
- Recuperación de contraseña simulada con mensajes visibles.
- Tablero adaptativo con grilla de frases rápidas.
- Conversión de texto a voz mediante `TextToSpeech`.
- Tema claro/oscuro, controles grandes y estados que no dependen únicamente del sonido.

## Credenciales de demostración

- Correo: `demo@comunicafacil.cl`
- Contraseña: `Acceso123`

## Requisitos de compilación

- Android Studio Quail 3 o compatible.
- JDK 17 o superior.
- Android SDK 37 y Build Tools 36.0.0 o superior.
- Android Gradle Plugin 9.2.0 y Gradle 9.4.1.

## Cómo revisar el proyecto

1. Abrir la carpeta raíz de `ComunicaFacil` en Android Studio.
2. Esperar la sincronización de Gradle.
3. Seleccionar un dispositivo con Android 8.0 (API 26) o superior.
4. Ejecutar la configuración `app`.

Para pruebas automatizadas locales:

```powershell
.\gradlew.bat testDebugUnitTest
```

Para generar el APK de depuración:

```powershell
.\gradlew.bat assembleDebug
```

## Alcance y consideraciones

En este avance implementé la autenticación y la recuperación de contraseña como simulaciones locales en memoria. La aplicación no guarda credenciales de forma persistente ni reemplaza un backend seguro. El arreglo de cinco posiciones responde al requisito académico y sus datos se reinician al cerrar el proceso de la aplicación.

Para comprobar el funcionamiento ejecuté cuatro pruebas unitarias, compilé el APK y probé las cuatro vistas principales en un emulador Android. En el documento de respuesta incluí capturas de Login, Registro, Recuperación y Comunicación, además de una explicación del código principal. Adjunto el código fuente, el APK y la documentación solicitada para su revisión.

Gracias por revisar mi trabajo.

Matías Andrés Muñoz Soto
