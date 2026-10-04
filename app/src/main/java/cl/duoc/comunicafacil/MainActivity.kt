package cl.duoc.comunicafacil

import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.content.Intent
import android.content.ActivityNotFoundException
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private var lector: TextToSpeech? = null
    private var vozLista = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lector = TextToSpeech(this) { estado -> vozLista = estado == TextToSpeech.SUCCESS }
        val backend: Backend = if (FirebaseApp.initializeApp(this) != null) BackendFirebase(this) else BackendLocal(this)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF245A81), background = Color.White)) {
                Aplicacion(backend)
            }
        }
    }

    override fun onDestroy() {
        lector?.stop()
        lector?.shutdown()
        super.onDestroy()
    }

    private fun hablar(texto: String, idioma: String): String {
        if (!vozLista) return "La voz no está disponible. Puedes mostrar el mensaje en pantalla."
        val resultado = lector?.setLanguage(if (idioma == "Español") Locale.forLanguageTag("es-CL") else Locale.US)
        if (resultado == TextToSpeech.LANG_MISSING_DATA || resultado == TextToSpeech.LANG_NOT_SUPPORTED) {
            return "Instala una voz para este idioma en los ajustes de Android."
        }
        return if (lector?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "mensaje") == TextToSpeech.SUCCESS)
            "Mensaje enviado al lector de voz." else "No se pudo reproducir el mensaje."
    }

    @Composable
    private fun Aplicacion(backend: Backend) {
        var pantalla by rememberSaveable { mutableStateOf(if (backend.actual() != null) "Comunicar" else "Login") }
        var aviso by rememberSaveable { mutableStateOf("") }
        var mensaje by rememberSaveable { mutableStateOf("") }
        var idioma by rememberSaveable { mutableStateOf("Español") }
        var grande by rememberSaveable { mutableStateOf(true) }
        var historial by remember { mutableStateOf<List<Mensaje>>(emptyList()) }
        var ocupado by remember { mutableStateOf(false) }
        var editar by remember { mutableStateOf<Mensaje?>(null) }
        var quitar by remember { mutableStateOf<Mensaje?>(null) }
        var borrarTodo by remember { mutableStateOf(false) }
        val usuario = backend.actual()

        LaunchedEffect(Unit) {
            if (backend.actual() == null && (pantalla == "Comunicar" || pantalla == "Historial")) pantalla = "Login"
        }

        fun cargar() {
            ocupado = true
            backend.consultar { lista, error ->
                ocupado = false
                if (error != null) aviso = error else historial = lista.orEmpty()
            }
        }
        fun accion(exito: String, operacion: ((String?) -> Unit) -> Unit) {
            ocupado = true
            operacion { error ->
                ocupado = false
                aviso = error ?: exito
                if (error == null) cargar()
            }
        }
        LaunchedEffect(pantalla) {
            if (pantalla == "Comunicar" || pantalla == "Historial") cargar()
        }

        fun navegar(destino: String) { pantalla = destino; aviso = "" }

        val escuchar = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val texto = resultado.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
                if (!texto.isNullOrBlank()) { mensaje = texto; aviso = "Texto recibido. Puedes corregirlo antes de mostrarlo." }
            } else aviso = "No se recibió texto. Puedes escribir el mensaje."
        }

        BackHandler(pantalla != "Login") {
            if (pantalla == "Historial") navegar("Comunicar")
            else { backend.salir(); mensaje = ""; historial = emptyList(); navegar("Login") }
        }

        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.safeDrawingPadding().imePadding().fillMaxSize().verticalScroll(rememberScrollState())
                .padding(20.dp).widthIn(max = 620.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Comunica Fácil", style = MaterialTheme.typography.headlineMedium)
                Text("Apoyo para comunicarte con texto y voz", style = MaterialTheme.typography.bodyMedium)
                HorizontalDivider()
                Text(pantalla, style = MaterialTheme.typography.titleLarge)
                if (ocupado) LinearProgressIndicator(Modifier.fillMaxWidth())
                if (aviso.isNotEmpty()) {
                    Text(aviso, Modifier.testTag("aviso"), color = MaterialTheme.colorScheme.primary)
                }
                when (pantalla) {
                    "Login" -> {
                        var correo by rememberSaveable { mutableStateOf("") }
                        var clave by rememberSaveable { mutableStateOf("") }
                        Campo("Correo", correo, { correo = it }, "correo", tipo = KeyboardType.Email)
                        Campo("Contraseña", clave, { clave = it }, "clave", secreto = true)
                        Button(onClick = {
                            ocupado = true
                            backend.ingresar(correo, clave) { error ->
                                ocupado = false
                                if (error != null) aviso = error else navegar("Comunicar")
                            }
                        }, Modifier.fillMaxWidth().testTag("ingresar"), enabled = !ocupado) { Text("Ingresar") }
                        TextButton(onClick = { navegar("Registro") }, Modifier.testTag("irRegistro")) { Text("Crear cuenta") }
                        TextButton(onClick = { navegar("Recuperar contraseña") }, Modifier.testTag("irRecuperar")) { Text("Olvidé mi contraseña") }
                        Text(if (backend.remoto) "Crea tu cuenta para conservar tus mensajes y acceder desde otro dispositivo."
                            else "Modo local de demostración. Se pueden registrar hasta 5 usuarios en este dispositivo.")
                        if (!backend.remoto) Text("Usuarios registrados: ${RegistroUsuarios(this@MainActivity).usuarios.filterNotNull().size}/5", Modifier.testTag("cantidad"))
                    }
                    "Registro" -> {
                        var nombre by rememberSaveable { mutableStateOf("") }
                        var correo by rememberSaveable { mutableStateOf("") }
                        var clave by rememberSaveable { mutableStateOf("") }
                        var pin by rememberSaveable { mutableStateOf("") }
                        var acepta by rememberSaveable { mutableStateOf(false) }
                        Campo("Nombre", nombre, { nombre = it }, "nombre")
                        Campo("Correo", correo, { correo = it }, "correo", tipo = KeyboardType.Email)
                        Campo("Contraseña (mínimo 6)", clave, { clave = it }, "clave", secreto = true)
                        if (!backend.remoto) Campo("PIN de recuperación (4 números)", pin, { pin = it }, "pin", secreto = true, tipo = KeyboardType.NumberPassword)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(acepta, { acepta = it }, Modifier.testTag("acepta"))
                            Text(if (backend.remoto) "Acepto guardar mi cuenta y mis mensajes en el servicio de la aplicación." else "Acepto guardar mi cuenta en este dispositivo.", Modifier.weight(1f))
                        }
                        Button(onClick = {
                            if (!acepta) aviso = if (backend.remoto) "Debes aceptar el almacenamiento de tus datos." else "Debes aceptar el almacenamiento local."
                            else {
                                ocupado = true
                                backend.registrar(nombre, correo, clave, if (backend.remoto) "0000" else pin) { error ->
                                    ocupado = false
                                    if (error != null) aviso = error
                                    else { navegar("Login"); aviso = "Cuenta registrada. Ya puedes ingresar." }
                                }
                            }
                        }, Modifier.fillMaxWidth().testTag("registrar"), enabled = !ocupado) { Text("Registrar") }
                        TextButton(onClick = { navegar("Login") }, Modifier.testTag("volver")) { Text("Volver al Login") }
                    }
                    "Recuperar contraseña" -> {
                        var correo by rememberSaveable { mutableStateOf("") }
                        var pin by rememberSaveable { mutableStateOf("") }
                        var clave by rememberSaveable { mutableStateOf("") }
                        Text(if (backend.remoto) "Te enviaremos un enlace para restablecer la contraseña a tu correo."
                            else "Ingresa el PIN que elegiste al registrarte. La contraseña se cambia en este dispositivo.")
                        Campo("Correo", correo, { correo = it }, "correo", tipo = KeyboardType.Email)
                        if (!backend.remoto) {
                            Campo("PIN de recuperación", pin, { pin = it }, "pin", secreto = true, tipo = KeyboardType.NumberPassword)
                            Campo("Nueva contraseña", clave, { clave = it }, "clave", secreto = true)
                        }
                        Button(onClick = {
                            ocupado = true
                            backend.recuperar(correo, pin, clave) { error ->
                                ocupado = false
                                if (error != null) aviso = error
                                else { navegar("Login"); aviso = if (backend.remoto) "Si el correo tiene una cuenta, recibirás un enlace de recuperación." else "Contraseña actualizada." }
                            }
                        }, Modifier.fillMaxWidth().testTag("recuperar"), enabled = !ocupado) { Text(if (backend.remoto) "Enviar enlace" else "Cambiar contraseña") }
                        TextButton(onClick = { navegar("Login") }, Modifier.testTag("volver")) { Text("Volver al Login") }
                    }
                    "Comunicar" -> {
                        Text("Hola, ${usuario?.nombre.orEmpty()}")
                        Campo("Escribe tu mensaje", mensaje, { mensaje = it }, "mensaje", variasLineas = true)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                accion("Mensaje guardado y listo para mostrar.") { fin -> backend.crear(mensaje, fin) }
                            }, Modifier.weight(1f).testTag("mostrar"), enabled = !ocupado) { Text("Mostrar") }
                            Button(onClick = { aviso = if (mensaje.isBlank()) "Escribe un mensaje primero." else hablar(mensaje, idioma) }, Modifier.weight(1f).testTag("hablar")) { Text("Hablar") }
                        }
                        OutlinedButton(onClick = {
                            val intento = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (idioma == "Español") "es-CL" else "en-US")
                            }
                            try { escuchar.launch(intento) }
                            catch (_: ActivityNotFoundException) { aviso = "Este dispositivo no tiene reconocimiento de voz. Puedes escribir el mensaje." }
                        }, Modifier.fillMaxWidth().testTag("escuchar")) { Text("Escuchar y pasar a texto") }
                        if (mensaje.isNotBlank()) {
                            Card(Modifier.fillMaxWidth()) {
                                Text(mensaje, Modifier.padding(16.dp).testTag("textoVisible"), fontSize = if (grande) 28.sp else 18.sp)
                            }
                        }
                        Text("Frases rápidas", style = MaterialTheme.typography.titleMedium)
                        val frases = listOf("Hola, ¿cómo estás?", "Necesito ayuda", "Por favor, escribe", "Muchas gracias")
                        frases.chunked(2).forEach { fila ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                fila.forEach { frase ->
                                    OutlinedButton(onClick = { mensaje = frase }, Modifier.weight(1f)) { Text(frase) }
                                }
                            }
                        }
                        var abierto by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(onClick = { abierto = true }) { Text("Idioma: $idioma") }
                            DropdownMenu(abierto, { abierto = false }) {
                                listOf("Español", "English").forEach { opcion ->
                                    DropdownMenuItem(text = { Text(opcion) }, onClick = { idioma = opcion; abierto = false })
                                }
                            }
                        }
                        Text("Tamaño del mensaje")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(!grande, { grande = false }); Text("Normal")
                            RadioButton(grande, { grande = true }); Text("Grande")
                        }
                        TextButton(onClick = { navegar("Historial") }, Modifier.testTag("irHistorial")) { Text("Ver historial (${historial.size})") }
                        TextButton(onClick = {
                            backend.salir(); mensaje = ""; historial = emptyList(); navegar("Login")
                        }, Modifier.testTag("salir"), enabled = !ocupado) { Text("Cerrar sesión") }
                    }
                    "Historial" -> {
                        Text("Últimos 10 mensajes guardados de tu cuenta")
                        Row(Modifier.fillMaxWidth()) {
                            Text("N.º", Modifier.width(48.dp)); Text("Mensaje", Modifier.weight(1f))
                        }
                        HorizontalDivider()
                        if (historial.isEmpty()) Text("Todavía no hay mensajes.")
                        historial.forEachIndexed { indice, item ->
                            Row(Modifier.fillMaxWidth()) {
                                Text("${indice + 1}", Modifier.width(48.dp)); Text(item.texto, Modifier.weight(1f))
                            }
                            Row {
                                TextButton(onClick = { editar = item }, Modifier.testTag("editar_${item.id}"), enabled = !ocupado) { Text("Editar") }
                                TextButton(onClick = { quitar = item }, Modifier.testTag("eliminar_${item.id}"), enabled = !ocupado) { Text("Eliminar") }
                            }
                            HorizontalDivider()
                        }
                        OutlinedButton(onClick = { borrarTodo = true }, Modifier.testTag("borrarHistorial"), enabled = !ocupado) { Text("Borrar historial") }
                        TextButton(onClick = { navegar("Comunicar") }, Modifier.testTag("volverComunicar")) { Text("Volver a comunicar") }
                    }
                }
            }
        }
        editar?.let { item ->
            var texto by remember(item.id) { mutableStateOf(item.texto) }
            AlertDialog(onDismissRequest = { editar = null }, title = { Text("Editar mensaje") },
                text = { Campo("Mensaje", texto, { texto = it }, "mensajeEditado", variasLineas = true) },
                confirmButton = { TextButton(onClick = {
                    val error = Validador.mensaje(texto)
                    if (error != null) aviso = error else { editar = null; accion("Mensaje actualizado.") { backend.modificar(item.id, texto, it) } }
                }) { Text("Guardar") } }, dismissButton = { TextButton(onClick = { editar = null }) { Text("Cancelar") } })
        }
        if (quitar != null || borrarTodo) AlertDialog(onDismissRequest = { quitar = null; borrarTodo = false },
            title = { Text(if (borrarTodo) "Borrar historial" else "Eliminar mensaje") }, text = { Text("Esta acción elimina los mensajes seleccionados. ¿Quieres continuar?") },
            confirmButton = { TextButton(onClick = {
                val id = quitar?.id; val todo = borrarTodo
                quitar = null; borrarTodo = false
                accion("Mensajes eliminados.") { if (todo) backend.borrar(it) else backend.eliminar(id!!, it) }
            }) { Text("Eliminar") } }, dismissButton = { TextButton(onClick = { quitar = null; borrarTodo = false }) { Text("Cancelar") } })
    }
}

@Composable
private fun Campo(etiqueta: String, valor: String, cambiar: (String) -> Unit, identificador: String,
                  secreto: Boolean = false, tipo: KeyboardType = KeyboardType.Text, variasLineas: Boolean = false) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(value = valor, onValueChange = cambiar, label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth().testTag(identificador), singleLine = !variasLineas,
        minLines = if (variasLineas) 2 else 1,
        visualTransformation = if (secreto && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (secreto && tipo == KeyboardType.Text) KeyboardType.Password else tipo),
        trailingIcon = if (secreto) { { TextButton(onClick = { visible = !visible }) { Text(if (visible) "Ocultar" else "Ver") } } } else null)
}
