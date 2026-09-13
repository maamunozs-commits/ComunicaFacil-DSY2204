package cl.duoc.comunicafacil

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.speech.RecognizerIntent
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FlujoAplicacionTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()

    private fun escribir(campo: String, texto: String) {
        regla.onNodeWithTag(campo).performScrollTo().performTextReplacement(texto)
    }

    private fun pulsar(boton: String) {
        regla.onNodeWithTag(boton).performScrollTo().performClick()
    }

    private fun captura(nombre: String) {
        regla.runOnUiThread {
            val teclado = regla.activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            teclado.hideSoftInputFromWindow(regla.activity.window.decorView.windowToken, 0)
        }
        regla.waitForIdle()
        Thread.sleep(500)
        val imagen = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        val carpeta = File(regla.activity.getExternalFilesDir(null), "capturas").apply { mkdirs() }
        File(carpeta, "$nombre.png").outputStream().use { imagen.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test
    fun registroAccesoRecuperacionYComunicacion() {
        regla.activity.getSharedPreferences("cuentas", Context.MODE_PRIVATE).edit().clear().commit()
        regla.activityRule.scenario.recreate()
        regla.waitForIdle()
        captura("01_login")
        pulsar("ingresar")
        regla.onNodeWithTag("aviso").assertTextEquals("Correo o contraseña incorrectos.")
        pulsar("irRegistro")
        pulsar("registrar")
        regla.onNodeWithTag("aviso").assertTextEquals("Debes aceptar el almacenamiento local.")
        pulsar("volver")
        val nombres = listOf("Matias", "Ana", "Luis", "Carla", "Pedro")
        nombres.forEachIndexed { indice, nombre ->
            pulsar("irRegistro")
            escribir("nombre", nombre)
            escribir("correo", "${nombre.lowercase()}@ejemplo.cl")
            escribir("clave", "Clave123")
            escribir("pin", "1234")
            pulsar("acepta")
            if (indice == 0) {
                regla.onNodeWithTag("nombre").performScrollTo()
                captura("02_registro")
            }
            pulsar("registrar")
            regla.onNodeWithTag("aviso").assertTextEquals("Cuenta registrada. Ya puedes ingresar.")
        }
        regla.onNodeWithTag("cantidad").assertTextEquals("Usuarios registrados: 5/5")
        captura("03_cinco_usuarios")
        pulsar("irRegistro")
        escribir("nombre", "Sexto")
        escribir("correo", "matias@ejemplo.cl")
        escribir("clave", "Clave123")
        escribir("pin", "1234")
        pulsar("acepta")
        pulsar("registrar")
        regla.onNodeWithTag("aviso").assertTextEquals("Este correo ya está registrado.")
        escribir("correo", "sexto@ejemplo.cl")
        pulsar("registrar")
        regla.onNodeWithTag("aviso").assertTextEquals("Se alcanzó el máximo de 5 usuarios.")
        pulsar("volver")
        pulsar("irRecuperar")
        escribir("correo", "matias@ejemplo.cl")
        escribir("pin", "9999")
        escribir("clave", "Nueva123")
        pulsar("recuperar")
        regla.onNodeWithTag("aviso").assertTextEquals("El correo o el PIN no coinciden.")
        escribir("pin", "1234")
        regla.onNodeWithTag("correo").performScrollTo()
        captura("04_recuperar")
        pulsar("recuperar")
        regla.onNodeWithTag("aviso").assertTextEquals("Contraseña actualizada.")
        escribir("correo", "matias@ejemplo.cl")
        escribir("clave", "Clave123")
        pulsar("ingresar")
        regla.onNodeWithTag("aviso").assertTextEquals("Correo o contraseña incorrectos.")
        escribir("clave", "Nueva123")
        pulsar("ingresar")
        pulsar("mostrar")
        regla.onNodeWithTag("aviso").assertTextEquals("Escribe un mensaje primero.")
        escribir("mensaje", "Hola, necesito ayuda para llegar a la sala.")
        pulsar("mostrar")
        regla.onNodeWithTag("textoVisible").assertTextEquals("Hola, necesito ayuda para llegar a la sala.")
        regla.onNodeWithTag("mensaje").performScrollTo()
        captura("05_comunicar")
        pulsar("irHistorial")
        regla.onNodeWithText("Hola, necesito ayuda para llegar a la sala.").assertExists()
        captura("06_historial")
        regla.activityRule.scenario.recreate()
        regla.waitForIdle()
        regla.onNodeWithText("Hola, necesito ayuda para llegar a la sala.").assertExists()
        pulsar("volverComunicar")
        pulsar("hablar")
        regla.onNodeWithTag("aviso").assertExists()
        val intento = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        if (intento.resolveActivity(regla.activity.packageManager) == null) {
            pulsar("escuchar")
            regla.waitForIdle()
            regla.onNodeWithTag("aviso").assertTextContains("reconocimiento de voz", substring = true)
        }
        pulsar("salir")
        regla.activityRule.scenario.recreate()
        regla.waitForIdle()
        regla.onNodeWithTag("cantidad").assertTextEquals("Usuarios registrados: 5/5")
        val guardado = RegistroUsuarios(regla.activity)
        assertEquals(5, guardado.usuarios.filterNotNull().size)
        assertNotNull(guardado.ingresar("MATIAS@EJEMPLO.CL", "Nueva123"))
        assertNull(guardado.ingresar("matias@ejemplo.cl", "Clave123"))
        assertNotEquals("Nueva123", guardado.usuarios[0]?.clave)
        escribir("correo", "matias@ejemplo.cl")
        escribir("clave", "Nueva123")
        pulsar("ingresar")
        pulsar("irHistorial")
        regla.onNodeWithText("Todavía no hay mensajes.").assertExists()
        pulsar("volverComunicar")
        regla.onNodeWithText("Necesito ayuda").performScrollTo().performClick()
        regla.onNodeWithTag("mensaje").performScrollTo()
        captura("07_frase_rapida")
    }
}
