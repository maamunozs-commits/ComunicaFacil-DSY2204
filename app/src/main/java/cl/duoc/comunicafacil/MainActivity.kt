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

class MainActivity : ComponentActivity() {
    private var lector: TextToSpeech? = null
    private var vozLista = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lector = TextToSpeech(this) { estado -> vozLista = estado == TextToSpeech.SUCCESS }
        val registro = RegistroUsuarios(this)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF245A81), background = Color.White)) {
                Aplicacion(registro)
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
    private fun Aplicacion(registro: RegistroUsuarios) {
        var pantalla by rememberSaveable { mutableStateOf("Login") }
        var correoActivo by rememberSaveable { mutableStateOf("") }
        var aviso by rememberSaveable { mutableStateOf("") }
        var mensaje by rememberSaveable { mutableStateOf("") }
        var idioma by rememberSaveable { mutableStateOf("Español") }
        var grande by rememberSaveable { mutableStateOf(true) }
        var historial by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
        val usuario = registro.usuarios.filterNotNull().find { it.correo == correoActivo }

        fun navegar(destino: String) { pantalla = destino; aviso = "" }

        val escuchar = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val texto = resultado.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
                if (!texto.isNullOrBlank()) { mensaje = texto; aviso = "Texto recibido. Puedes corregirlo antes de mostrarlo." }
            } else aviso = "No se recibió texto. Puedes escribir el mensaje."
        }

        BackHandler(pantalla != "Login") {
            if (pantalla == "Historial") navegar("Comunicar")
            else { correoActivo = ""; mensaje = ""; historial = arrayListOf(); navegar("Login") }
        }

        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.safeDrawingPadding().imePadding().fillMaxSize().verticalScroll(rememberScrollState())
                .padding(20.dp).widthIn(max = 620.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Comunica Fácil", style = MaterialTheme.typography.headlineMedium)
                Text("Apoyo para comunicarte con texto y voz", style = MaterialTheme.typography.bodyMedium)
                HorizontalDivider()
                Text(pantalla, style = MaterialTheme.typography.titleLarge)
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
                            val cuenta = registro.ingresar(correo, clave)
                            if (cuenta == null) aviso = "Correo o contraseña incorrectos."
                            else { correoActivo = cuenta.correo; navegar("Comunicar") }
                        }, Modifier.fillMaxWidth().testTag("ingresar")) { Text("Ingresar") }
                        TextButton(onClick = { navegar("Registro") }, Modifier.testTag("irRegistro")) { Text("Crear cuenta") }
                        TextButton(onClick = { navegar("Recuperar contraseña") }, Modifier.testTag("irRecuperar")) { Text("Olvidé mi contraseña") }
                        Text("Primero crea tu cuenta. Se pueden registrar hasta 5 usuarios en este dispositivo.")
                        Text("Usuarios registrados: ${registro.usuarios.filterNotNull().size}/5", Modifier.testTag("cantidad"))
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
                        Campo("PIN de recuperación (4 números)", pin, { pin = it }, "pin", secreto = true, tipo = KeyboardType.NumberPassword)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(acepta, { acepta = it }, Modifier.testTag("acepta"))
                            Text("Acepto guardar mi cuenta en este dispositivo.", Modifier.weight(1f))
                        }
                        Button(onClick = {
                            aviso = if (!acepta) "Debes aceptar el almacenamiento local."
                            else registro.registrar(nombre, correo, clave, pin) ?: ""
                            if (aviso.isEmpty()) { navegar("Login"); aviso = "Cuenta registrada. Ya puedes ingresar." }
                        }, Modifier.fillMaxWidth().testTag("registrar")) { Text("Registrar") }
                        TextButton(onClick = { navegar("Login") }, Modifier.testTag("volver")) { Text("Volver al Login") }
                    }
                    "Recuperar contraseña" -> {
                        var correo by rememberSaveable { mutableStateOf("") }
                        var pin by rememberSaveable { mutableStateOf("") }
                        var clave by rememberSaveable { mutableStateOf("") }
                        Text("Ingresa el PIN que elegiste al registrarte. La contraseña se cambia en este dispositivo.")
                        Campo("Correo", correo, { correo = it }, "correo", tipo = KeyboardType.Email)
                        Campo("PIN de recuperación", pin, { pin = it }, "pin", secreto = true, tipo = KeyboardType.NumberPassword)
                        Campo("Nueva contraseña", clave, { clave = it }, "clave", secreto = true)
                        Button(onClick = {
                            aviso = registro.recuperar(correo, pin, clave) ?: ""
                            if (aviso.isEmpty()) { navegar("Login"); aviso = "Contraseña actualizada." }
                        }, Modifier.fillMaxWidth().testTag("recuperar")) { Text("Cambiar contraseña") }
                        TextButton(onClick = { navegar("Login") }, Modifier.testTag("volver")) { Text("Volver al Login") }
                    }
                    "Comunicar" -> {
                        Text("Hola, ${usuario?.nombre.orEmpty()}")
                        Campo("Escribe tu mensaje", mensaje, { mensaje = it }, "mensaje", variasLineas = true)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                if (mensaje.isBlank()) aviso = "Escribe un mensaje primero."
                                else { historial = ArrayList((historial + mensaje.trim()).takeLast(10)); aviso = "Mensaje listo para mostrar." }
                            }, Modifier.weight(1f).testTag("mostrar")) { Text("Mostrar") }
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
                            correoActivo = ""; mensaje = ""; historial = arrayListOf(); navegar("Login")
                        }, Modifier.testTag("salir")) { Text("Cerrar sesión") }
                    }
                    "Historial" -> {
                        Text("Últimos 10 mensajes de esta sesión")
                        Row(Modifier.fillMaxWidth()) {
                            Text("N.º", Modifier.width(48.dp)); Text("Mensaje", Modifier.weight(1f))
                        }
                        HorizontalDivider()
                        if (historial.isEmpty()) Text("Todavía no hay mensajes.")
                        historial.forEachIndexed { indice, texto ->
                            Row(Modifier.fillMaxWidth()) {
                                Text("${indice + 1}", Modifier.width(48.dp)); Text(texto, Modifier.weight(1f))
                            }
                            HorizontalDivider()
                        }
                        OutlinedButton(onClick = { historial = arrayListOf() }, Modifier.testTag("borrarHistorial")) { Text("Borrar historial") }
                        TextButton(onClick = { navegar("Comunicar") }, Modifier.testTag("volverComunicar")) { Text("Volver a comunicar") }
                    }
                }
            }
        }
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
