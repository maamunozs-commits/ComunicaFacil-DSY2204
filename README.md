# Comunica Fácil

Proyecto de la semana 5: Integrando Kotlin a la aplicación móvil con Android Studio.

- Estudiante: Matías Muñoz.
- Institución: Duoc UC.
- Carrera: Ingeniería en Desarrollo de Software.
- Asignatura: Desarrollo de Aplicaciones Móviles, DSY2204.
- Sección: 001A.
- Profesor: Miguel Puebla.

La aplicación ayuda a una persona con discapacidad auditiva a comunicarse mediante mensajes escritos, texto grande y lectura en voz alta. Tiene Login, Registro, recuperación de contraseña y un historial sencillo.

## Abrir el proyecto

1. Descomprimir la entrega, descomprimir `ComunicaFacil_proyecto.zip` y abrir la carpeta `ComunicaFacil` desde Android Studio.
2. Esperar la sincronización de Gradle y aceptar la instalación del SDK que solicite Android Studio.
3. Usar el JDK incluido con Android Studio y un teléfono o emulador con Android 8.0 o superior.
4. Presionar Run para instalar la aplicación.

El proyecto usa Kotlin, Jetpack Compose, Material 3, Android Gradle Plugin 9.2.0, Gradle 9.4.1 y compileSdk 37. Para sincronizar las dependencias se necesita internet. También se incluye un APK de prueba que se puede instalar directamente.

## Cómo usarlo

Primero hay que entrar en Crear cuenta y completar nombre, correo, contraseña y PIN de cuatro números. La contraseña debe tener al menos seis caracteres. Después se puede ingresar con el correo y la contraseña.

En Comunicar se escribe un mensaje y se presiona Mostrar para agregarlo al historial. El texto también aparece en una tarjeta grande. Hablar lo envía al lector de voz de Android. Escuchar y pasar a texto abre el servicio de reconocimiento instalado en el teléfono. Si no está disponible, aparece un aviso y se puede escribir normalmente.

Las frases rápidas completan el mensaje. El menú permite escoger español o inglés y los botones de selección cambian el tamaño del texto. El historial muestra los últimos diez mensajes de la sesión y se borra al cerrar sesión.

Para recuperar el acceso se ingresa el correo, el PIN elegido al registrarse y una contraseña nueva. No se envían correos: es una recuperación local para esta actividad.

## Los cinco usuarios

`RegistroUsuarios.kt` tiene un `arrayOfNulls<Usuario>(5)`. El formulario Registro incorpora las cuentas al arreglo y las guarda con SharedPreferences en formato JSON. Al abrir la aplicación se cargan otra vez. La sexta cuenta y los correos repetidos se rechazan.

La instalación nueva empieza sin cuentas. En las pruebas se registraron estos datos ficticios desde la interfaz:

| Nombre | Correo | Contraseña inicial | PIN |
| --- | --- | --- | --- |
| Matías | matias@ejemplo.cl | Clave123 | 1234 |
| Ana | ana@ejemplo.cl | Clave123 | 1234 |
| Luis | luis@ejemplo.cl | Clave123 | 1234 |
| Carla | carla@ejemplo.cl | Clave123 | 1234 |
| Pedro | pedro@ejemplo.cl | Clave123 | 1234 |

La prueba cambia la contraseña de Matías a `Nueva123`. Estos datos sirven para repetir la demostración; no vienen precargados en el APK. Las contraseñas y el PIN se guardan como resúmenes SHA-256, sin mostrarse en la interfaz. Es un ejercicio local: no cuenta con servidor, verificación de identidad ni protección adecuada para cuentas reales.

## Archivos principales

| Archivo | Función |
| --- | --- |
| `MainActivity.kt` | Pantallas, navegación, mensajes y servicios de voz. |
| `Usuario.kt` | Datos de cada cuenta. |
| `RegistroUsuarios.kt` | Arreglo de cinco usuarios, validación y guardado. |
| `FlujoAplicacionTest.kt` | Prueba de registro, acceso, recuperación e historial. |
| `documentacion/capturas` | Capturas obtenidas de la aplicación en ejecución. |

En Kotlin se usan funciones, parámetros, una clase de datos, valores nulos, expresiones lambda, `when`, un arreglo y colecciones. `find` busca la cuenta; `any` detecta duplicados; `filterNotNull` obtiene los usuarios registrados; `chunked` organiza las frases en una grilla y `takeLast` limita el historial.

## Compilar y probar

Desde la terminal del proyecto en Windows:

```powershell
.\gradlew.bat assembleDebug lintDebug
.\gradlew.bat connectedDebugAndroidTest
```

La segunda instrucción necesita un emulador o teléfono conectado y ejecuta una prueba que borra las cuentas de la aplicación antes de registrar los cinco usuarios ficticios. Conviene usar un emulador dedicado a la actividad.

Las pruebas se realizaron en Android 11. El reconocimiento de voz no estaba instalado en ese emulador, por lo que se verificó el aviso de servicio no disponible. La reproducción audible y la transcripción con un micrófono real requieren revisión en un teléfono con los servicios de voz instalados. Las cuentas se pierden si se borran los datos de la app o se desinstala.

## Repositorio Git

La entrega de la semana 5 est? en [GitHub, rama entrega-semana-5](https://github.com/maamunozs-commits/ComunicaFacil-DSY2204/tree/entrega-semana-5). Esta rama contiene el proyecto completo, las capturas, el APK y el informe PDF en `entrega/`.

Para descargar el proyecto con su historial:

```powershell
git clone --branch entrega-semana-5 https://github.com/maamunozs-commits/ComunicaFacil-DSY2204.git ComunicaFacil
cd ComunicaFacil
git log --oneline
```

El archivo `ComunicaFacil_proyecto.zip` tambi?n incluye `ComunicaFacil.bundle` dentro de la carpeta del proyecto. Este archivo permite recuperar el historial sin conexi?n a GitHub. Desde la carpeta que contiene el bundle:

```powershell
git clone ComunicaFacil.bundle ComunicaFacil_con_historial
```

Para trabajar una modificaci?n en una rama:

```powershell
git switch -c ajuste-pantallas
git add .
git commit -m "Ajustar pantallas"
```

## Referencias

- [Jetpack Compose](https://developer.android.com/develop/ui/compose/documentation).
- [TextToSpeech](https://developer.android.com/reference/android/speech/tts/TextToSpeech).
- [RecognizerIntent](https://developer.android.com/reference/android/speech/RecognizerIntent).
- [Kotlin integrado en Android Gradle Plugin](https://developer.android.com/build/migrate-to-built-in-kotlin).
