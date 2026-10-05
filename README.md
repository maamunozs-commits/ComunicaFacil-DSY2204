# Comunica Fácil semana 8

Matías Muñoz · DSY2204 · 001A · Miguel Puebla · Duoc UC.

La versión 1.2 funciona con Firebase Authentication y Cloud Firestore reales. Conserva Kotlin, Jetpack Compose, texto grande, TextToSpeech, reconocimiento de voz, frases rápidas, idioma y tamaño del proyecto S5. Agrega CRUD remoto de mensajes y datos básicos de sesión en SharedPreferences.

## Abrir y usar

Abrir la raíz en Android Studio, sincronizar Gradle con internet y ejecutar en Android 8 o superior. El proyecto ZIP de entrega incluye `app/google-services.json` del proyecto institucional `comunica-facil-s8-matias-4d9d5`. En Git ese archivo se omite: usar la copia del ZIP o descargarla desde Firebase Console con la cuenta maa.munozs@duocuc.cl. AGP 9.2.0, Gradle 9.4.1, compileSdk 37, targetSdk 36; JDK 17 o superior, validado con JBR de Android Studio.

Crear una cuenta con nombre, correo, contraseña de al menos seis caracteres y consentimiento. Ingresar, escribir un mensaje y pulsar Mostrar para guardarlo en Firestore. Historial consulta los diez últimos y permite editar, eliminar o borrar todo con confirmación. Cerrar sesión conserva los mensajes remotos. Recuperar contraseña solicita a Firebase un enlace por correo. No se exige PIN en el backend remoto. Las cuentas locales S5 no se migran automáticamente.

Se necesita conexión para confirmar operaciones. El progreso permanece mientras la escritura espera la respuesta del servidor. Sin configuración Firebase, el código utiliza BackendLocal y muestra expresamente la demostración local de cinco cuentas y PIN.

## Verificar y firmar

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME=Join-Path $env:LOCALAPPDATA 'Android\Sdk'
.\gradlew.bat testDebugUnitTest connectedDebugAndroidTest lintDebug
.\scripts\firmar-release.ps1
```

Las pruebas utilizan un emulador dedicado y cuentas ficticias @example.com. Los tests del backend remoto crean y eliminan sus propios datos; el flujo UI conserva una cuenta y un mensaje demostrativo para revisión. Las pruebas de reglas usan instancias SDK independientes y no comparten su estado con la UI. No usar estas credenciales de prueba para información personal.

20 pruebas aprobadas y una prueba del antiguo flujo UI local omitida porque el APK está configurado para Firebase. El flujo remoto también pasó sobre el APK release firmado. Lint: cero errores y 14 advertencias. La voz audible, el micrófono en un teléfono físico y la recepción del enlace en una casilla real no se verificaron.

La firma release usa la misma clave privada fuera del proyecto, en `%USERPROFILE%\.android\comunicafacil-firma`. `firma.xml` está protegido mediante DPAPI para la cuenta Windows propietaria. Respaldar esa carpeta de forma privada para futuras actualizaciones. El ZIP y Git no incluyen keystore ni contraseñas de firma. En otro equipo se puede compilar debug; para actualizar el release se necesita la clave original.

## Distribución

Repositorio: https://github.com/maamunozs-commits/ComunicaFacil-DSY2204/tree/entrega-semana-8

APK release con Firebase: https://github.com/maamunozs-commits/ComunicaFacil-DSY2204/releases/tag/v1.2.0-s8

El ZIP académico incluye APK debug, APK release firmado, proyecto con configuración Firebase, Word, PDF y bundle Git. La presentación en AVA queda a cargo del estudiante. La publicación se realizó en GitHub Releases; no se ha publicado en Google Play.

Configuración: [FIREBASE.md](documentacion/semana8/FIREBASE.md). Resultados: [PRUEBAS.md](documentacion/semana8/PRUEBAS.md).
