package cl.duoc.comunicafacil

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source

class BackendFirebase(contexto: Context, app: FirebaseApp = FirebaseApp.getInstance()) : Backend {
    override val remoto = true
    private val auth = FirebaseAuth.getInstance(app)
    private val db = FirebaseFirestore.getInstance(app)
    private val sesion = contexto.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    override fun actual(): Cuenta? = auth.currentUser?.let {
        Cuenta(it.uid, it.displayName.orEmpty(), it.email.orEmpty())
    }
    private fun guardarSesion() {
        actual()?.let { sesion.edit().putString("uid", it.uid).putString("correo", it.correo).putString("nombre", it.nombre).apply() }
    }
    private fun error(): String = "No se pudo completar la operación. Revisa tu conexión y vuelve a intentarlo."
    override fun registrar(nombre: String, correo: String, clave: String, pin: String, fin: (String?) -> Unit) {
        val validacion = Validador.registro(nombre, correo, clave, pin)
        if (validacion != null) { fin(validacion); return }
        auth.createUserWithEmailAndPassword(Validador.correo(correo), clave).addOnCompleteListener { tarea ->
            if (!tarea.isSuccessful) { fin("No se pudo registrar la cuenta. Revisa el correo, la clave y tu conexión."); return@addOnCompleteListener }
            val usuario = auth.currentUser!!
            usuario.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(nombre.trim()).build()).addOnCompleteListener { perfil ->
                // Auth es la fuente del perfil; no se guardan contraseñas ni PIN en Firestore.
                auth.signOut(); sesion.edit().clear().apply()
                fin(if (perfil.isSuccessful) null else "La cuenta se creó, pero no se guardó el nombre. Puedes ingresar con tu correo.")
            }
        }
    }
    override fun ingresar(correo: String, clave: String, fin: (String?) -> Unit) {
        if (correo.isBlank() || clave.isBlank()) { fin("Correo o contraseña incorrectos."); return }
        auth.signInWithEmailAndPassword(Validador.correo(correo), clave).addOnCompleteListener {
            if (it.isSuccessful) { guardarSesion(); fin(null) } else fin("Correo o contraseña incorrectos, o conexión no disponible.")
        }
    }
    override fun recuperar(correo: String, pin: String, clave: String, fin: (String?) -> Unit) {
        if (!Validador.correo(correo).matches(Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))) { fin("Escribe un correo válido."); return }
        auth.sendPasswordResetEmail(Validador.correo(correo)).addOnCompleteListener {
            fin(if (it.isSuccessful) null else error())
        }
    }
    override fun salir() { auth.signOut(); sesion.edit().clear().apply() }
    private fun mensajes() = db.collection("usuarios").document(auth.currentUser!!.uid).collection("mensajes")
    override fun consultar(fin: (List<Mensaje>?, String?) -> Unit) {
        if (actual() == null) { fin(null, "Debes iniciar sesión."); return }
        mensajes().orderBy("creado", Query.Direction.DESCENDING).limit(10).get(Source.SERVER).addOnCompleteListener { tarea ->
            if (tarea.isSuccessful) fin(tarea.result.documents.map {
                Mensaje(it.id, it.getString("texto").orEmpty(), it.getLong("creado") ?: 0)
            }.reversed(), null) else fin(null, error())
        }
    }
    override fun crear(texto: String, fin: (String?) -> Unit) {
        val validacion = Validador.mensaje(texto)
        if (validacion != null) { fin(validacion); return }
        if (actual() == null) { fin("Debes iniciar sesión."); return }
        mensajes().add(mapOf("texto" to texto.trim(), "creado" to System.currentTimeMillis())).addOnCompleteListener { fin(if (it.isSuccessful) null else error()) }
    }
    override fun modificar(id: String, texto: String, fin: (String?) -> Unit) {
        val validacion = Validador.mensaje(texto)
        if (validacion != null) { fin(validacion); return }
        if (actual() == null) { fin("Debes iniciar sesión."); return }
        mensajes().document(id).update("texto", texto.trim()).addOnCompleteListener { fin(if (it.isSuccessful) null else error()) }
    }
    override fun eliminar(id: String, fin: (String?) -> Unit) {
        if (actual() == null) { fin("Debes iniciar sesión."); return }
        mensajes().document(id).delete().addOnCompleteListener { fin(if (it.isSuccessful) null else error()) }
    }
    override fun borrar(fin: (String?) -> Unit) {
        if (actual() == null) { fin("Debes iniciar sesión."); return }
        mensajes().limit(400).get(Source.SERVER).addOnCompleteListener { lectura ->
            if (!lectura.isSuccessful) { fin(error()); return@addOnCompleteListener }
            // La pantalla conserva solo los diez últimos; el borrado elimina el historial completo.
            val documentos = lectura.result.documents
            if (documentos.isEmpty()) { fin(null); return@addOnCompleteListener }
            val lote = db.batch()
            documentos.forEach { lote.delete(it.reference) }
            lote.commit().addOnCompleteListener {
                if (it.isSuccessful) borrar(fin) else fin(error())
            }
        }
    }
}
