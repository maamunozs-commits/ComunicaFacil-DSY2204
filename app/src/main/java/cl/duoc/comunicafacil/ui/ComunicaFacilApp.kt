package cl.duoc.comunicafacil.ui

import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.HearingDisabled
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.comunicafacil.data.RepositorioUsuarios
import cl.duoc.comunicafacil.data.ResultadoRegistro
import cl.duoc.comunicafacil.data.Usuario
import java.util.Locale
import kotlinx.coroutines.launch

private enum class Pantalla { INGRESO, REGISTRO, RECUPERACION, COMUNICACION }

@Composable
fun AplicacionComunicaFacil() {
    val repositorio = remember { RepositorioUsuarios() }
    var pantalla by rememberSaveable { mutableStateOf(Pantalla.INGRESO) }
    var nombre by rememberSaveable { mutableStateOf("") }

    BackHandler(pantalla != Pantalla.INGRESO) { pantalla = Pantalla.INGRESO }

    when (pantalla) {
        Pantalla.INGRESO -> VistaIngreso(
            repositorio,
            ingresar = { nombre = it.nombre; pantalla = Pantalla.COMUNICACION },
            registrar = { pantalla = Pantalla.REGISTRO },
            recuperar = { pantalla = Pantalla.RECUPERACION },
        )
        Pantalla.REGISTRO -> VistaRegistro(repositorio) { pantalla = Pantalla.INGRESO }
        Pantalla.RECUPERACION -> VistaRecuperacion(repositorio) { pantalla = Pantalla.INGRESO }
        Pantalla.COMUNICACION -> VistaComunicacion(nombre) {
            nombre = ""
            pantalla = Pantalla.INGRESO
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Estructura(
    titulo: String,
    subtitulo: String,
    volver: () -> Unit,
    contenido: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(titulo, fontWeight = FontWeight.Bold)
                        Text(subtitulo, style = MaterialTheme.typography.labelMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = volver) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        content = contenido,
    )
}

@Composable
private fun Logo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(76.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Outlined.HearingDisabled,
                    null,
                    Modifier.size(42.dp),
                    MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "ComunicaFácil",
            fontSize = 30.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            "Comunicación clara, visible y accesible",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun Aviso(texto: String) {
    Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Outlined.AccessibilityNew, null)
            Text(texto, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CampoClave(
    valor: String,
    cambiar: (String) -> Unit,
    etiqueta: String = "Contraseña",
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = valor,
        onValueChange = cambiar,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun VistaIngreso(
    repositorio: RepositorioUsuarios,
    ingresar: (Usuario) -> Unit,
    registrar: () -> Unit,
    recuperar: () -> Unit,
) {
    var correo by rememberSaveable { mutableStateOf(RepositorioUsuarios.CORREO_DEMO) }
    var clave by rememberSaveable { mutableStateOf(RepositorioUsuarios.CLAVE_DEMO) }
    var error by rememberSaveable { mutableStateOf("") }

    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()).imePadding().padding(24.dp, 48.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            Modifier.fillMaxWidth().widthIn(max = 560.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Logo()
            Spacer(Modifier.height(8.dp))
            Aviso("Las acciones importantes se confirman con texto e iconos, no solo con sonido.")
            OutlinedTextField(
                correo,
                { correo = it; error = "" },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Outlined.Email, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            CampoClave(clave, { clave = it; error = "" })
            if (error.isNotEmpty()) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics { contentDescription = "Error: $error" },
                )
            }
            Button(
                onClick = {
                    val usuario = repositorio.ingresar(correo, clave)
                    if (usuario == null) error = "Correo o contraseña incorrectos." else ingresar(usuario)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) { Text("Ingresar", fontSize = 17.sp) }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = recuperar) { Text("¿Olvidaste tu contraseña?") }
                TextButton(onClick = registrar) { Text("Crear cuenta") }
            }
            HorizontalDivider()
            Text(
                "Acceso de demostración: ${RepositorioUsuarios.CORREO_DEMO} / ${RepositorioUsuarios.CLAVE_DEMO}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun VistaRegistro(repositorio: RepositorioUsuarios, volver: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var preferencia by rememberSaveable { mutableStateOf("Texto y voz") }
    var listaAbierta by remember { mutableStateOf(false) }
    var alertas by rememberSaveable { mutableStateOf(true) }
    var vibracion by rememberSaveable { mutableStateOf(true) }
    var acepta by rememberSaveable { mutableStateOf(false) }
    var apoyo by rememberSaveable { mutableStateOf("Texto") }
    var mensaje by rememberSaveable { mutableStateOf("") }
    var exito by rememberSaveable { mutableStateOf(false) }

    Estructura("Crear cuenta", "Registro accesible", volver) { margen ->
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding()
                .padding(margen).padding(24.dp).widthIn(max = 720.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Aviso("Quedan ${repositorio.cupos()} de ${RepositorioUsuarios.MAX_USUARIOS} cupos en el arreglo local.")
            OutlinedTextField(
                nombre,
                { nombre = it; mensaje = "" },
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Outlined.Person, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                correo,
                { correo = it; mensaje = "" },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Outlined.Email, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            CampoClave(clave, { clave = it; mensaje = "" }, "Contraseña (mínimo 8 caracteres)")

            Text("Preferencia de comunicación", fontWeight = FontWeight.SemiBold)
            Box {
                OutlinedButton({ listaAbierta = true }, Modifier.fillMaxWidth()) {
                    Text(preferencia, Modifier.weight(1f), textAlign = TextAlign.Start)
                    Text("▾", Modifier.semantics { contentDescription = "Abrir opciones" })
                }
                DropdownMenu(listaAbierta, { listaAbierta = false }) {
                    listOf("Texto y voz", "Solo texto", "Alertas visuales").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = { preferencia = it; listaAbierta = false },
                        )
                    }
                }
            }

            Text("Modo de apoyo principal", fontWeight = FontWeight.SemiBold)
            listOf("Texto", "Pictogramas").forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(apoyo == it, { apoyo = it })
                    Text(it)
                }
            }
            Text("Ajustes de accesibilidad", fontWeight = FontWeight.SemiBold)
            FilaSeleccion("Usar alertas visuales", alertas) { alertas = it }
            FilaSeleccion("Usar vibración como apoyo", vibracion) { vibracion = it }
            FilaSeleccion("Acepto las condiciones de uso académico", acepta) { acepta = it }

            if (mensaje.isNotEmpty()) {
                Text(
                    mensaje,
                    color = if (exito) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Button(
                onClick = {
                    if (!acepta) {
                        exito = false
                        mensaje = "Debes aceptar las condiciones para continuar."
                    } else {
                        val resultado = repositorio.registrar(Usuario(nombre, correo, clave, preferencia))
                        exito = resultado == ResultadoRegistro.Exito
                        mensaje = when (resultado) {
                            ResultadoRegistro.Exito -> "Cuenta registrada correctamente."
                            ResultadoRegistro.CorreoRepetido -> "El correo ya está registrado."
                            ResultadoRegistro.SinCupos -> "Se alcanzó el máximo de cinco usuarios."
                            ResultadoRegistro.DatosInvalidos -> "Revisa nombre, correo y contraseña."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) { Text("Registrar usuario", fontSize = 17.sp) }

            if (exito) {
                FilledTonalButton(volver, Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.CheckCircle, null)
                    Text("Ir al inicio de sesión", Modifier.padding(start = 8.dp))
                }
            }
            ResumenUsuarios(repositorio.usuarios().size, repositorio.cupos())
        }
    }
}

@Composable
private fun FilaSeleccion(texto: String, marcado: Boolean, cambiar: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(marcado, cambiar)
        Text(texto, Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun ResumenUsuarios(registrados: Int, disponibles: Int) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Resumen del arreglo de usuarios", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            FilaResumen("Capacidad total", "5")
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            FilaResumen("Usuarios registrados", "$registrados")
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            FilaResumen("Cupos disponibles", "$disponibles")
        }
    }
}

@Composable
private fun FilaResumen(texto: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(texto)
        Text(valor, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun VistaRecuperacion(repositorio: RepositorioUsuarios, volver: () -> Unit) {
    var correo by rememberSaveable { mutableStateOf("") }
    var resultado by rememberSaveable { mutableStateOf("") }
    var encontrado by rememberSaveable { mutableStateOf(false) }

    Estructura("Recuperar contraseña", "Ayuda de acceso", volver) { margen ->
        Box(
            Modifier.fillMaxSize().padding(margen).verticalScroll(rememberScrollState())
                .imePadding().padding(24.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                Modifier.fillMaxWidth().widthIn(max = 560.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Aviso("La confirmación se muestra en pantalla. Este prototipo no envía correos reales.")
                OutlinedTextField(
                    correo,
                    { correo = it; resultado = "" },
                    label = { Text("Correo registrado") },
                    leadingIcon = { Icon(Icons.Outlined.Email, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {
                        encontrado = repositorio.buscar(correo) != null
                        resultado = if (encontrado) {
                            "Cuenta encontrada. Se enviaría un enlace seguro de recuperación."
                        } else {
                            "No existe una cuenta asociada a ese correo."
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) { Text("Buscar cuenta", fontSize = 17.sp) }
                if (resultado.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            if (encontrado) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.errorContainer,
                        ),
                    ) { Text(resultado, Modifier.padding(18.dp), fontWeight = FontWeight.Medium) }
                }
                TextButton(volver, Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Volver a iniciar sesión")
                }
            }
        }
    }
}

private data class Frase(val titulo: String, val mensaje: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VistaComunicacion(nombre: String, salir: () -> Unit) {
    val contexto = LocalContext.current
    val avisos = remember { SnackbarHostState() }
    val corrutina = rememberCoroutineScope()
    var mensaje by rememberSaveable { mutableStateOf("") }
    var vozLista by remember { mutableStateOf(false) }
    var lector by remember { mutableStateOf<TextToSpeech?>(null) }
    val frases = remember {
        listOf(
            Frase("Saludo", "Hola, mucho gusto."),
            Frase("Ayuda", "Necesito ayuda, por favor."),
            Frase("Repetir", "¿Puedes repetirlo más despacio?"),
            Frase("Gracias", "Muchas gracias por tu ayuda."),
            Frase("Emergencia", "Es una emergencia. Necesito asistencia."),
            Frase("Espera", "Por favor, espera un momento."),
        )
    }

    DisposableEffect(contexto) {
        lateinit var motor: TextToSpeech
        motor = TextToSpeech(contexto) {
            vozLista = it == TextToSpeech.SUCCESS &&
                motor.setLanguage(Locale.forLanguageTag("es-CL")) >= TextToSpeech.LANG_AVAILABLE
        }
        lector = motor
        onDispose { motor.stop(); motor.shutdown() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(avisos) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hola, $nombre", fontWeight = FontWeight.Bold)
                        Text("Tablero de comunicación", style = MaterialTheme.typography.labelMedium)
                    }
                },
                actions = {
                    IconButton(salir) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, "Cerrar sesión")
                    }
                },
            )
        },
    ) { margen ->
        Column(
            Modifier.fillMaxSize().padding(margen).padding(horizontal = 20.dp).imePadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Aviso("Escribe un mensaje o elige una frase rápida. La app puede leerlo en voz alta.")
            OutlinedTextField(
                mensaje,
                { mensaje = it },
                label = { Text("Mensaje para comunicar") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    val aviso = when {
                        mensaje.isBlank() -> "Escribe o selecciona un mensaje."
                        !vozLista -> "El lector de voz aún no está disponible."
                        lector?.speak(mensaje, TextToSpeech.QUEUE_FLUSH, null, "mensaje") == TextToSpeech.SUCCESS ->
                            "Reproduciendo mensaje en voz alta."
                        else -> "No fue posible reproducir el mensaje."
                    }
                    corrutina.launch { avisos.showSnackbar(aviso) }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) {
                Icon(Icons.Outlined.RecordVoiceOver, null)
                Text("Leer en voz alta", Modifier.padding(start = 10.dp), fontSize = 17.sp)
            }
            Text("Frases rápidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(142.dp),
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
            ) {
                items(frases) { frase ->
                    Card(
                        onClick = { mensaje = frase.mensaje },
                        modifier = Modifier.height(104.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                    ) {
                        Column(
                            Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(frase.titulo, fontWeight = FontWeight.Bold)
                            Text(frase.mensaje, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}
