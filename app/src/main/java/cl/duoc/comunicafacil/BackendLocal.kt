package cl.duoc.comunicafacil

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** Conserva el prototipo de la semana 5 y permite ensayar sin una cuenta de nube. */
class BackendLocal(contexto: Context) : Backend {
    override val remoto = false
    private val registro = RegistroUsuarios(contexto)
    private val sesion = contexto.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    private val datos = contexto.getSharedPreferences("mensajes", Context.MODE_PRIVATE)
    override fun actual(): Cuenta? {
        val correo = sesion.getString("correo", null) ?: return null
        val usuario = registro.usuarios.filterNotNull().find { it.correo == correo } ?: return null
        return Cuenta(correo, usuario.nombre, correo)
    }
    override fun registrar(nombre: String, correo: String, clave: String, pin: String, fin: (String?) -> Unit) {
        fin(Validador.registro(nombre, correo, clave, pin) ?: registro.registrar(nombre, correo, clave, pin))
    }
    override fun ingresar(correo: String, clave: String, fin: (String?) -> Unit) {
        val usuario = registro.ingresar(correo, clave)
        if (usuario == null) fin("Correo o contraseña incorrectos.")
        else { sesion.edit().putString("correo", usuario.correo).apply(); fin(null) }
    }
    override fun recuperar(correo: String, pin: String, clave: String, fin: (String?) -> Unit) = fin(registro.recuperar(correo, pin, clave))
    override fun salir() { sesion.edit().clear().apply() }
    private fun clave() = actual()?.uid ?: error("Debes iniciar sesión.")
    private fun leer(): List<Mensaje> {
        val json = JSONArray(datos.getString(clave(), "[]"))
        return (0 until json.length()).map { i -> json.getJSONObject(i).let { Mensaje(it.getString("id"), it.getString("texto"), it.getLong("creado")) } }
    }
    private fun guardar(lista: List<Mensaje>) {
        val json = JSONArray()
        lista.forEach { json.put(JSONObject().put("id", it.id).put("texto", it.texto).put("creado", it.creado)) }
        datos.edit().putString(clave(), json.toString()).apply()
    }
    override fun consultar(fin: (List<Mensaje>?, String?) -> Unit) {
        if (actual() == null) fin(null, "Debes iniciar sesión.") else fin(leer().takeLast(10), null)
    }
    override fun crear(texto: String, fin: (String?) -> Unit) {
        val error = Validador.mensaje(texto)
        if (error != null) fin(error)
        else if (actual() == null) fin("Debes iniciar sesión.")
        else { guardar((leer() + Mensaje(UUID.randomUUID().toString(), texto.trim(), System.currentTimeMillis())).takeLast(10)); fin(null) }
    }
    override fun modificar(id: String, texto: String, fin: (String?) -> Unit) {
        val error = Validador.mensaje(texto)
        if (error != null) { fin(error); return }
        if (actual() == null) { fin("Debes iniciar sesión."); return }
        val lista = leer()
        if (lista.none { it.id == id }) { fin("Mensaje no encontrado."); return }
        guardar(lista.map { if (it.id == id) it.copy(texto = texto.trim()) else it }); fin(null)
    }
    override fun eliminar(id: String, fin: (String?) -> Unit) {
        if (actual() == null) { fin("Debes iniciar sesión."); return }
        val lista = leer()
        if (lista.none { it.id == id }) { fin("Mensaje no encontrado."); return }
        guardar(lista.filterNot { it.id == id }); fin(null)
    }
    override fun borrar(fin: (String?) -> Unit) {
        if (actual() == null) fin("Debes iniciar sesión.") else { guardar(emptyList()); fin(null) }
    }
}
