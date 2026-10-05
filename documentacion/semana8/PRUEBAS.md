# Resultados de la versión 1.2 con Firebase

Entorno: emulador AOSP Android 11 API 30, 720 × 1280, Firebase real en Spark. Ejecución local del 04/10/2026; los XML usan UTC y pueden indicar 05/10/2026.

| Suite | Aprobadas | Omitidas | Fallos | Tiempo |
| --- | ---: | ---: | ---: | ---: |
| ValidadorTest JUnit | 12 | 0 | 0 | 0,019 s |
| BackendLocalTest | 5 | 0 | 0 | 0,144 s |
| BackendFirebaseTest | 2 | 0 | 0 | 13,025 s |
| FlujoFirebaseTest | 1 | 0 | 0 | 19,095 s |
| FlujoAplicacionTest local anterior | 0 | 1 | 0 | 1,683 s |

Total: 20 aprobadas, una omitida, cero fallos y cero errores. La suite instrumentada completa duró 35,038 s. Los XML están en `evidencias-firebase/`. La prueba UI local se omite expresamente porque requiere el PIN y el límite de cinco cuentas de S5, que no corresponden al servicio remoto.

Las pruebas remotas verifican CRUD real, logout/nuevo ingreso, conservación del ID y la fecha al editar, recarga y consulta desde una segunda instancia SDK con su propio Auth y caché. Las reglas rechazan operaciones sin sesión, acceso ajeno, texto de 1001 caracteres y cambio de fecha. La segunda cuenta puede crear y consultar sus propios mensajes. Las pruebas negativas se aíslan mediante una instancia SDK por prueba, para no contaminar el estado del flujo de interfaz.

La UI comprueba registro con consentimiento, acceso, Mostrar, Historial, recreación de Activity, edición, eliminación confirmada, frase rápida, logout, nuevo acceso y solicitud de recuperación. La recepción del correo en una casilla real queda fuera de esta evidencia.

Se repitió el mismo flujo sobre `app-release.apk` firmado: OK (1 test), 17,252 s. El APK test se firmó con el mismo certificado exclusivamente para esa validación y no se distribuye. Las capturas `capturas-firebase/01` a `11` provienen de esa ejecución release; `12` a `14` provienen de la consola real. No se fabricaron capturas mediante generación de imágenes. `contrato-compose.png` es una ilustración de las firmas Kotlin, distinta de las evidencias de funcionamiento.

Compilación debug/release aprobada. Firma APK v2 verificada, certificado SHA-256 `457f725246a0dabd760b695913f60e37123062c8137a3bec84551553cae12b4a`. Instalación release y arranque correctos. Inicio frío: debug 1072 ms y release 480 ms, una muestra por configuración; no permiten generalizar ni atribuir una mejora estadística. Lint: cero errores y 14 advertencias, registradas en XML.

No se midieron FPS, memoria, latencia por petición ni audio físico. Se conserva la alternativa de texto. No se declara una prueba con dos teléfonos físicos ni una publicación en Google Play. Los archivos `capturas/` y `evidencias/` anteriores corresponden a la demostración local histórica, no a esta validación remota.
