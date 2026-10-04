# Comunica Fácil semana 8

Matías Muñoz · DSY2204 · 001A · Miguel Puebla · Duoc UC.

La aplicación conserva el diseño y las funciones de texto, voz, frases, idioma y tamaño de la semana 5. La semana 8 agrega una interfaz de backend Kotlin, autenticación y persistencia Firebase preparadas, sesión con SharedPreferences y CRUD de mensajes desde Historial. El APK distribuido funciona en modo local de demostración; aún falta crear y configurar Firebase.

## Abrir y usar

Abrir la raíz del proyecto en Android Studio, sincronizar Gradle con conexión a internet y ejecutar en Android 8 o superior. Se utiliza AGP 9.2.0, Gradle 9.4.1, compileSdk 37, targetSdk 36 y JDK 17 o superior (validado con el JBR de Android Studio). Las versiones exactas están en los archivos Gradle.

Crear una cuenta con nombre, correo, clave de seis caracteres o más y PIN de cuatro números. El modo local admite cinco cuentas por dispositivo; las cuentas existentes de la semana 5 se siguen leyendo. Al ingresar, escribir y pulsar Mostrar crea un mensaje. Historial permite consultar, editar, eliminar y borrar todos con confirmación. Se conservan los diez últimos mensajes por cuenta. Cerrar sesión conserva esos mensajes; el nuevo acceso los recarga. Recuperar contraseña mantiene el PIN en modo local. Las contraseñas locales siguen siendo SHA-256 para el ejercicio y no deben usarse con cuentas reales.

La pantalla indica expresamente si está en modo local. Para activar el backend remoto, seguir [FIREBASE.md](FIREBASE.md). En ese modo las cuentas se autentican con FirebaseAuth, los mensajes usan Cloud Firestore y la recuperación envía un enlace por correo. Las preferencias no contienen contraseñas, PIN ni tokens Firebase; guardan los datos básicos de sesión. Las reglas incluidas autorizan únicamente al propietario.

## Verificar y firmar

```powershell
.\gradlew.bat testDebugUnitTest connectedDebugAndroidTest lintDebug
.\scripts\firmar-release.ps1
```

Las pruebas instrumentadas borran los datos de las cuentas ficticias en el emulador: usar un dispositivo dedicado. La clave release y la contraseña protegida para Windows permanecen fuera del repositorio en `%USERPROFILE%\.android\comunicafacil-firma`. `firma.xml` está protegido mediante DPAPI para la cuenta Windows que lo creó. Respaldar esa carpeta de forma privada: la misma clave se requiere para las actualizaciones. El script de firma requiere esa carpeta; otros equipos deben usar su propia clave o recibir un respaldo privado. No hay contraseñas ni keystores en el ZIP ni en Git.

Resultados y límites en [PRUEBAS.md](PRUEBAS.md): 18 pruebas aprobadas y APK release instalado con firma v2 verificada. Firebase y audio físico requieren validación adicional.

## Distribución

Repositorio: https://github.com/maamunozs-commits/ComunicaFacil-DSY2204/tree/entrega-semana-8

Release de demostración: https://github.com/maamunozs-commits/ComunicaFacil-DSY2204/releases/tag/v1.1.0-s8

El informe institucional y el ZIP se entregan al profesor mediante AVA. La release distribuye el APK local y el código; no anuncia una versión con Firebase activado. No es una publicación en Google Play. Antes de una publicación en Google Play hay que revisar sus requisitos vigentes y adaptar el paquete y target API cuando corresponda.
