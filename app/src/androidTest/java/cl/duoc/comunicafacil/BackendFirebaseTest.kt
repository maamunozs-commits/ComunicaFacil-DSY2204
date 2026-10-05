package cl.duoc.comunicafacil

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Pruebas de integración contra el proyecto real; no usa emuladores Firebase. */
@RunWith(AndroidJUnit4::class)
class BackendFirebaseTest {
    private val contexto = ApplicationProvider.getApplicationContext<android.content.Context>()
    private lateinit var backend: BackendFirebase
    private lateinit var app: FirebaseApp
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val clave = "PruebaS8!123"
    private fun <T> esperar(tarea: Task<T>): T = Tasks.await(tarea, 60, TimeUnit.SECONDS)
    private fun operacion(accion: ((String?) -> Unit) -> Unit): String? {
        val listo = CountDownLatch(1)
        var error: String? = null
        accion { error = it; listo.countDown() }
        assertTrue("El servidor debe responder", listo.await(60, TimeUnit.SECONDS))
        return error
    }
    private fun cuenta(): String {
        val correo = "s8-${UUID.randomUUID()}@example.com"
        assertNull(operacion { backend.registrar("Alumno prueba", correo, clave, "0000", it) })
        assertNull(backend.actual())
        assertNull(operacion { backend.ingresar(correo, clave, it) })
        return correo
    }
    private fun consultar(): List<Mensaje> {
        val listo = CountDownLatch(1)
        var lista: List<Mensaje>? = null
        var error: String? = null
        backend.consultar { datos, fallo -> lista = datos; error = fallo; listo.countDown() }
        assertTrue(listo.await(60, TimeUnit.SECONDS))
        assertNull(error)
        return lista!!
    }
    @Before fun iniciar() {
        FirebaseApp.initializeApp(contexto)
        app = FirebaseApp.initializeApp(contexto, FirebaseApp.getInstance().options, "test-${UUID.randomUUID()}")
        auth = FirebaseAuth.getInstance(app)
        db = FirebaseFirestore.getInstance(app)
        backend = BackendFirebase(contexto, app)
    }
    @After fun cerrarInstanciaDePrueba() {
        auth.signOut()
        esperar(db.terminate())
        app.delete()
    }
    @Test fun crudSesionYLecturaIndependienteDelServidor() {
        val correo = cuenta()
        val uid = backend.actual()!!.uid
        assertNull(operacion { backend.crear("Mensaje inicial desde Kotlin", it) })
        val inicial = consultar().single()
        assertNull(operacion { backend.modificar(inicial.id, "Mensaje editado en Firestore", it) })
        assertEquals("Mensaje editado en Firestore", consultar().single().texto)
        assertEquals(uid, BackendFirebase(contexto, app).actual()!!.uid)
        // Otra instancia del SDK, con su propio Auth y caché, consulta Source.SERVER.
        val secundaria = FirebaseApp.initializeApp(contexto, FirebaseApp.getInstance().options, "lectura-${UUID.randomUUID()}")
        val auth2 = FirebaseAuth.getInstance(secundaria)
        val db2 = FirebaseFirestore.getInstance(secundaria)
        try {
            esperar(auth2.signInWithEmailAndPassword(correo, clave))
            val remoto = esperar(db2.collection("usuarios").document(uid).collection("mensajes")
                .document(inicial.id).get(Source.SERVER))
            assertEquals("Mensaje editado en Firestore", remoto.getString("texto"))
            backend.salir()
            assertNull(BackendFirebase(contexto, app).actual())
            assertNull(operacion { backend.ingresar(correo, clave, it) })
            assertEquals(inicial.id, consultar().single().id)
            assertNull(operacion { backend.eliminar(inicial.id, it) })
            assertTrue(consultar().isEmpty())
            repeat(2) { indice -> assertNull(operacion { backend.crear("Mensaje $indice", it) }) }
            assertNull(operacion { backend.borrar(it) })
            assertTrue(consultar().isEmpty())
        } finally {
            auth2.signOut()
            esperar(db2.terminate())
            secundaria.delete()
            auth.currentUser?.let { esperar(it.delete()) }
            backend.salir()
        }
    }
    @Test fun reglasRechazanOtraCuentaSinSesionYDatosInvalidos() {
        val correo = cuenta()
        val uid = auth.currentUser!!.uid
        assertNull(operacion { backend.crear("Dato privado de la cuenta A", it) })
        val mensaje = consultar().single()
        val referencia = db.collection("usuarios").document(uid).collection("mensajes").document(mensaje.id)
        fun denegada(tarea: Task<*>) {
            try { esperar(tarea); fail("Las reglas deben rechazar esta operación") }
            catch (error: java.util.concurrent.ExecutionException) {
                assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED,
                    (error.cause as FirebaseFirestoreException).code)
            }
        }
        denegada(referencia.update("texto", "x".repeat(1001)))
        denegada(referencia.update("creado", 0L))
        backend.salir()
        denegada(referencia.get(Source.SERVER))
        cuenta()
        assertNull(operacion { backend.crear("Dato propio de la cuenta B", it) })
        assertEquals("Dato propio de la cuenta B", consultar().single().texto)
        denegada(referencia.get(Source.SERVER))
        denegada(referencia.update("texto", "Intento de otra cuenta"))
        assertNull(operacion { backend.borrar(it) })
        esperar(auth.currentUser!!.delete())
        assertNull(operacion { backend.ingresar(correo, clave, it) })
        assertNull(operacion { backend.borrar(it) })
        esperar(auth.currentUser!!.delete())
        backend.salir()
    }
}
