package cl.duoc.comunicafacil

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackendLocalTest {
    private lateinit var contexto: Context
    private lateinit var backend: BackendLocal
    @Before fun preparar() {
        contexto = ApplicationProvider.getApplicationContext()
        listOf("cuentas", "sesion", "mensajes").forEach { contexto.getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().commit() }
        backend = BackendLocal(contexto)
        backend.registrar("Matías", "matias@ejemplo.cl", "Clave123", "1234") { assertNull(it) }
    }
    private fun entrar() = backend.ingresar("matias@ejemplo.cl", "Clave123") { assertNull(it) }
    private fun mensajes(): List<Mensaje> {
        var resultado = emptyList<Mensaje>()
        backend.consultar { lista, error -> assertNull(error); resultado = lista!! }
        return resultado
    }
    @Test fun rechazaCrudSinSesion() {
        backend.crear("Hola") { assertNotNull(it) }
        backend.consultar { lista, error -> assertNull(lista); assertNotNull(error) }
        backend.modificar("no existe", "Hola") { assertNotNull(it) }
        backend.eliminar("no existe") { assertNotNull(it) }
        backend.borrar { assertNotNull(it) }
    }
    @Test fun cicloCrudYRecarga() {
        entrar()
        backend.crear(" Hola ") { assertNull(it) }
        val original = mensajes().single()
        assertEquals("Hola", original.texto)
        backend.modificar(original.id, "Necesito ayuda") { assertNull(it) }
        backend = BackendLocal(contexto)
        assertNotNull(backend.actual())
        assertEquals("Necesito ayuda", mensajes().single().texto)
        assertEquals(original.id, mensajes().single().id)
        backend.eliminar(original.id) { assertNull(it) }
        assertTrue(mensajes().isEmpty())
    }
    @Test fun cierreSesionConservaDatosYAislaCuentas() {
        entrar(); backend.crear("Privado") { assertNull(it) }; backend.salir()
        assertNull(BackendLocal(contexto).actual())
        backend.registrar("Ana", "ana@ejemplo.cl", "Clave123", "1234") { assertNull(it) }
        backend.ingresar("ana@ejemplo.cl", "Clave123") { assertNull(it) }
        assertTrue(mensajes().isEmpty())
        backend.salir(); entrar(); assertEquals("Privado", mensajes().single().texto)
    }
    @Test fun limiteYBorradoCompleto() {
        entrar()
        (1..12).forEach { i -> backend.crear("Mensaje $i") { assertNull(it) } }
        assertEquals(10, mensajes().size)
        assertEquals("Mensaje 3", mensajes().first().texto)
        backend.borrar { assertNull(it) }; assertTrue(mensajes().isEmpty())
    }
    @Test fun actualizacionInvalidaNoDestruyeMensaje() {
        entrar(); backend.crear("Conservar") { assertNull(it) }
        val id = mensajes().single().id
        backend.modificar(id, " ") { assertNotNull(it) }
        backend.modificar("desconocido", "Hola") { assertNotNull(it) }
        backend.eliminar("desconocido") { assertNotNull(it) }
        assertEquals("Conservar", mensajes().single().texto)
    }
}
