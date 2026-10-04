# Pruebas de la semana 8

Fecha: 04/10/2026. Emulador AOSP Android 11, API 30, 720 × 1280. Código Kotlin y Compose, versión 1.1 (versionCode 2). Los datos son ficticios.

`assembleRelease testDebugUnitTest connectedDebugAndroidTest lintDebug` terminó con BUILD SUCCESSFUL. Los informes XML verifican 18 pruebas, sin fallos ni errores: 12 unitarias JUnit (0,029 s), cinco instrumentadas del backend local (0,162 s) y un flujo de interfaz Compose/AndroidJUnit4 (24,872 s). El flujo se repitió directamente con AndroidJUnitRunner para recuperar las capturas: OK (1 test), 24,237 s.

| Grupo | Verificaciones |
| --- | --- |
| JUnit | Normalización de correo, campos válidos, nombre corto, correo incompleto y con espacios, clave corta y su límite, PIN alfanumérico y longitud, mensaje vacío y límites de 1000/1001 caracteres. |
| Backend local | CRUD sin sesión rechazado; crear, consultar, editar y eliminar; recarga de instancia; sesión persistida; cierre de sesión; aislamiento de cuentas; diez mensajes recientes; borrado completo; edición inválida y registros inexistentes. |
| Interfaz | Cinco registros, duplicado y sexto rechazados; recuperación por PIN; clave anterior rechazada; mensaje, historial y recreación; cierre de sesión; nuevo acceso conserva historial; editar y eliminar desde diálogos; frases rápidas; ausencia de reconocimiento con aviso. |

Lint: cero errores y 14 advertencias. Se refieren a target API anterior a la última disponible, versiones de dependencias, reglas de extracción de datos, icono de aplicación y sugerencias KTX. `allowBackup=false` continúa configurado. Las advertencias no se presentan como resueltas.

El APK release se instaló en el emulador y abrió Login correctamente. `apksigner verify --verbose --print-certs` confirmó la firma v2, un firmante, RSA 3072 bits. Certificado SHA-256: `457f725246a0dabd760b695913f60e37123062c8137a3bec84551553cae12b4a`. No utiliza el certificado debug.

Inicio en frío registrado mediante `adb shell am start -W`: debug 1725 ms; release 611 ms. Son muestras individuales del emulador y configuraciones distintas, sin equivalencia con un benchmark ni garantía de rendimiento. No se midieron FPS, consumo, latencia de Firebase ni tiempos de una transcripción real.

La activación de Firebase, pruebas de sus reglas en el servicio, acceso desde otro dispositivo y correo de recuperación permanecen pendientes. La salida de audio audible y la transcripción con micrófono requieren validación física. Los resultados locales no se atribuyen a Firebase. Capturas nuevas en `capturas/`; XML, logs de compilación, firma, instalación y arranque en `evidencias/`.
