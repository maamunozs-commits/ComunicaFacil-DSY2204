# Activar Firebase para Comunica Fácil

La integración Kotlin está preparada. El APK publicado es una demostración local porque todavía no se ha creado el proyecto Firebase. No contiene una configuración de nube ficticia.

1. Crear un proyecto en Firebase Console. Registrar la aplicación Android con el paquete `cl.duoc.comunicafacil`.
2. Descargar `google-services.json` y copiarlo en `app/`. El plugin Google Services se aplica cuando existe ese archivo.
3. En Authentication, habilitar el proveedor Correo electrónico/contraseña.
4. Crear Cloud Firestore y publicar el contenido de `firestore.rules` desde su editor de reglas. No dejar la base en modo de prueba.
5. Compilar e instalar nuevamente. Login deja de mostrar «Modo local de demostración». Crear una cuenta de prueba nueva: las cuentas de la semana 5 no se migran automáticamente y sus contraseñas locales no se envían a la nube.
6. Registrar e ingresar, crear un mensaje, consultar el historial, editarlo y eliminarlo. Cerrar y abrir la app; entrar desde un segundo dispositivo con la misma cuenta y comprobar la persistencia.
7. Comprobar la recuperación mediante correo. Firebase administra contraseñas y enlaces de recuperación; el PIN se conserva únicamente en el modo local.
8. Probar con una segunda cuenta: no debe leer ni modificar documentos de la primera. Probar acceso sin autenticar en el simulador de reglas: debe ser denegado. Probar cortes de conexión y confirmar el aviso sin asumir que una escritura pendiente fue confirmada por el servidor.
9. Repetir las pruebas de interfaz sobre Firebase. Las pruebas actuales se diseñaron para el backend local: usan PIN, cinco cuentas y el aviso de demostración; no acreditan el servicio remoto.
10. Generar otro APK release firmado con la misma clave y un `versionCode` mayor que 2. Publicarlo como actualización con nube activa solamente después de esas comprobaciones.

Los mensajes se guardan en `usuarios/{uid}/mensajes/{id}` con `texto` y `creado`. Las reglas exigen `request.auth.uid == uid`, rechazan campos adicionales y limitan el texto a 1000 caracteres. SharedPreferences guarda datos básicos de sesión; el acceso remoto depende de FirebaseAuth, nunca de un UID escrito en preferencias.

Las lecturas utilizan `Source.SERVER`; sin conexión muestran un error. Las escrituras de Firestore pueden quedar pendientes hasta recuperar conexión; el indicador de progreso permanece mientras no se confirme la tarea. El historial presenta los diez registros más recientes. Borrar historial elimina todos los mensajes de la cuenta en lotes de hasta 400 documentos.

Referencias: [configuración Android](https://firebase.google.com/docs/android/setup), [autenticación por contraseña](https://firebase.google.com/docs/auth/android/password-auth), [escrituras en Firestore](https://firebase.google.com/docs/firestore/manage-data/add-data), [reglas de seguridad](https://firebase.google.com/docs/firestore/security/get-started).
