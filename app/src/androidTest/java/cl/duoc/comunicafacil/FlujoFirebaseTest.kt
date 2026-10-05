package cl.duoc.comunicafacil

import android.content.Context
import android.graphics.Bitmap
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FlujoFirebaseTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()
    private fun escribir(campo: String, texto: String) =
        regla.onNodeWithTag(campo).performScrollTo().performTextReplacement(texto)
    private fun pulsar(boton: String) = regla.onNodeWithTag(boton).performScrollTo().performClick()
    private fun textoListo(texto: String) {
        regla.waitUntil(60_000) { regla.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty() }
    }
    private fun botonListo(tag: String) {
        regla.waitUntil(60_000) {
            regla.onAllNodesWithTag(tag).fetchSemanticsNodes().any {
                !it.config.contains(androidx.compose.ui.semantics.SemanticsProperties.Disabled)
            }
        }
    }
    private fun captura(nombre: String) {
        regla.runOnUiThread {
            val teclado = regla.activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            teclado.hideSoftInputFromWindow(regla.activity.window.decorView.windowToken, 0)
        }
        regla.waitForIdle()
        Thread.sleep(500)
        val imagen = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        val carpeta = File(regla.activity.getExternalFilesDir(null), "capturas-firebase").apply { mkdirs() }
        File(carpeta, "$nombre.png").outputStream().use { imagen.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
    @Test fun registroLoginCrudPersistenciaYLogoutEnFirebaseReal() {
        FirebaseAuth.getInstance().signOut()
        regla.activity.getSharedPreferences("sesion", Context.MODE_PRIVATE).edit().clear().commit()
        regla.activityRule.scenario.recreate()
        regla.waitForIdle()
        captura("01_login")
        pulsar("irRegistro")
        val correo = "s8-ui-${System.currentTimeMillis()}@example.com"
        escribir("nombre", "Matías Muñoz · prueba")
        escribir("correo", correo)
        escribir("clave", "PruebaS8!123")
        pulsar("acepta")
        regla.onNodeWithTag("nombre").performScrollTo()
        captura("02_registro")
        pulsar("registrar")
        textoListo("Cuenta registrada. Ya puedes ingresar.")
        captura("03_cuenta_creada")
        escribir("correo", correo)
        escribir("clave", "PruebaS8!123")
        pulsar("ingresar")
        botonListo("mostrar")
        assertNotNull(FirebaseAuth.getInstance().currentUser)
        escribir("mensaje", "Hola, necesito ayuda para llegar a la sala.")
        pulsar("mostrar")
        textoListo("Mensaje guardado y listo para mostrar.")
        botonListo("irHistorial")
        regla.onNodeWithTag("mensaje").performScrollTo()
        captura("05_comunicar")
        pulsar("irHistorial")
        textoListo("Hola, necesito ayuda para llegar a la sala.")
        botonListo("borrarHistorial")
        captura("06_historial")
        regla.activityRule.scenario.recreate()
        textoListo("Hola, necesito ayuda para llegar a la sala.")
        botonListo("borrarHistorial")
        regla.onNodeWithText("Editar").performScrollTo().performClick()
        regla.onNodeWithTag("mensajeEditado").performTextReplacement("Mensaje editado y guardado en Firebase.")
        regla.onNodeWithText("Guardar").performClick()
        textoListo("Mensaje editado y guardado en Firebase.")
        botonListo("borrarHistorial")
        captura("08_historial_editado")
        regla.onNodeWithText("Eliminar").performScrollTo().performClick()
        regla.onAllNodesWithText("Eliminar").onLast().performClick()
        textoListo("Mensajes eliminados.")
        botonListo("borrarHistorial")
        regla.onNodeWithText("Todavía no hay mensajes.").assertExists()
        captura("09_historial_eliminado")
        pulsar("volverComunicar")
        botonListo("mostrar")
        regla.onNodeWithText("Necesito ayuda").performScrollTo().performClick()
        regla.onNodeWithTag("mensaje").performScrollTo()
        captura("07_frase_rapida")
        pulsar("mostrar")
        textoListo("Mensaje guardado y listo para mostrar.")
        botonListo("salir")
        pulsar("salir")
        regla.activityRule.scenario.recreate()
        regla.onNodeWithTag("ingresar").assertExists()
        escribir("correo", correo)
        escribir("clave", "PruebaS8!123")
        pulsar("ingresar")
        botonListo("irHistorial")
        pulsar("irHistorial")
        textoListo("Necesito ayuda")
        botonListo("borrarHistorial")
        captura("10_historial_tras_reingreso")
        pulsar("volverComunicar")
        botonListo("salir")
        pulsar("salir")
        pulsar("irRecuperar")
        escribir("correo", correo)
        captura("04_recuperar")
        pulsar("recuperar")
        textoListo("Si el correo tiene una cuenta, recibirás un enlace de recuperación.")
        captura("11_solicitud_recuperacion")
        // La cuenta ficticia y un mensaje de demostración permanecen para revisión docente.
    }
}
