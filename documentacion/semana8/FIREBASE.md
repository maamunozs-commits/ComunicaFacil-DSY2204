# Firebase activo para Comunica Fácil

Proyecto `comunica-facil-s8-matias-4d9d5`, creado con maa.munozs@duocuc.cl. Plan Spark gratuito. App Android `cl.duoc.comunicafacil`. Authentication con Correo electrónico/contraseña habilitado. Cloud Firestore Standard, base `(default)`, región `southamerica-west1` (Santiago). Reglas de `firestore.rules` publicadas.

El proyecto ZIP contiene `app/google-services.json` descargado de ese Firebase real. Este archivo configura el SDK Android y no contiene la clave privada de firma ni una cuenta de servicio. Se omite de Git; para compilar desde Git, descargarlo desde la configuración de la app en Firebase Console o copiarlo desde el ZIP de entrega. Sin él, se inicia el modo local de S5.

## Datos y autorización

Firestore conserva `usuarios/{uid}/mensajes/{id}` con campos `texto` y `creado`. El documento padre usuario puede no existir: los mensajes pertenecen a su subcolección. La identidad procede de FirebaseAuth. Las reglas solo permiten al propietario leer, crear, editar y eliminar; rechazan campos extra, texto vacío o mayor que 1000 caracteres, y cambios de fecha en la edición. No se guardan contraseñas ni PIN en Firestore o SharedPreferences. Las preferencias solo conservan UID, nombre y correo.

La consulta usa `Source.SERVER`, orden descendente por fecha y límite de diez registros. La UI los presenta en orden cronológico. El borrado completo procesa lotes de hasta 400 documentos. Las escrituras esperan la confirmación; sin red pueden quedar pendientes hasta recuperar conexión.

## Comprobación realizada

`BackendFirebaseTest` verificó CRUD, sesión, otra instancia SDK con autenticación y caché independientes, acceso denegado sin sesión o desde otra cuenta, y rechazo de datos inválidos. No se usaron emuladores Firebase. `FlujoFirebaseTest` ejecutó registro, login, creación, consulta, recreación, edición, eliminación, logout, nuevo login y solicitud de recuperación. También pasó sobre el release firmado. La lectura independiente acredita persistencia en el servidor; no se presenta como una prueba con dos teléfonos físicos. La solicitud de recuperación aceptada no acredita entrega del correo en una casilla @example.com.

Los APK entregados ya están conectados; no hace falta que el profesor cree un proyecto Firebase para probarlos. Basta instalar y registrar su propia cuenta de prueba.

Referencias: [configuración Android](https://firebase.google.com/docs/android/setup), [autenticación por contraseña](https://firebase.google.com/docs/auth/android/password-auth), [Cloud Firestore](https://firebase.google.com/docs/firestore/manage-data/add-data), [reglas](https://firebase.google.com/docs/firestore/security/get-started).
