# Registro de pruebas

Fecha: 13/09/2026. Entorno: emulador Android 11 (API 30), 720 × 1280 píxeles.

La prueba `registroAccesoRecuperacionYComunicacion` se ejecutó en el emulador mediante AndroidJUnitRunner y terminó con `OK (1 test)`. Una misma prueba recorre el flujo y comprueba los siguientes resultados:

| Comprobación | Resultado |
| --- | --- |
| Acceso con credenciales vacías | Rechazado con aviso. |
| Registro sin aceptar el guardado | Rechazado con aviso. |
| Cinco cuentas creadas desde Registro | Arreglo con cinco usuarios. |
| Correo repetido | Rechazado. |
| Sexta cuenta | Rechazada por límite. |
| Recuperación con PIN incorrecto | Rechazada. |
| Recuperación con PIN correcto | Contraseña actualizada. |
| Acceso con contraseña anterior | Rechazado. |
| Acceso con contraseña nueva | Permitido. |
| Mensaje vacío | Aviso para escribir primero. |
| Mostrar mensaje | Texto visible y agregado al historial. |
| Recrear la actividad | Historial y cuentas conservados. |
| Reconocimiento de voz ausente | Aviso y alternativa de escritura. |
| Cerrar sesión | Historial limpio en el siguiente acceso. |
| Seleccionar frase rápida | Mensaje actualizado. |

También se revisó visualmente Login a 480 × 800 y 1080 × 1920 píxeles, con densidad de 240 dpi. El contenido se ajusta al ancho y la pantalla pequeña permite desplazarse. Las imágenes están en `capturas`.

`assembleDebug` y `lintDebug` terminaron correctamente. Lint no encontró errores y emitió siete advertencias: versiones más nuevas disponibles, configuración de copias de seguridad, icono de aplicación y una dependencia de prueba no utilizada. Se conservaron las versiones con las que se compiló el proyecto.

Una medición de inicio en frío con `adb shell am start -W` registró 1586 ms. Ese valor solo describe una ejecución del emulador.

La prueba llama al lector de voz y comprueba que la aplicación responda con un aviso. No verifica la reproducción audible. La transcripción con micrófono real y la salida de audio quedan pendientes de revisión en un teléfono que tenga los servicios correspondientes.
