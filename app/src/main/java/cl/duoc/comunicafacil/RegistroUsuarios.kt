package cl.duoc.comunicafacil

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.Locale

class RegistroUsuarios(contexto: Context) {
    private val preferencias = contexto.getSharedPreferences("cuentas", Context.MODE_PRIVATE)
    val usuarios = arrayOfNulls<Usuario>(5)

    init {
        val datos = JSONArray(preferencias.getString("usuarios", "[]"))
        for (indice in 0 until minOf(datos.length(), usuarios.size)) {
            val dato = datos.getJSONObject(indice)
            usuarios[indice] = Usuario(dato.getString("nombre"), dato.getString("correo"),
                dato.getString("clave"), dato.getString("pin"))
        }
    }

    private fun resumen(texto: String): String = MessageDigest.getInstance("SHA-256")
        .digest(texto.toByteArray()).joinToString("") { "%02x".format(it) }

    private fun normalizar(correo: String) = correo.trim().lowercase(Locale.ROOT)

    fun registrar(nombre: String, correo: String, clave: String, pin: String): String? {
        if (nombre.trim().length < 2) return "Escribe un nombre de al menos 2 letras."
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()) return "Escribe un correo válido."
        if (clave.length < 6) return "La contraseña debe tener al menos 6 caracteres."
        if (!pin.matches(Regex("[0-9]{4}"))) return "El PIN debe tener 4 números."
        if (usuarios.any { it?.correo == normalizar(correo) }) return "Este correo ya está registrado."
        val posicion = usuarios.indexOfFirst { it == null }
        if (posicion == -1) return "Se alcanzó el máximo de 5 usuarios."
        usuarios[posicion] = Usuario(nombre.trim(), normalizar(correo), resumen(clave), resumen(pin))
        guardar()
        return null
    }

    fun ingresar(correo: String, clave: String): Usuario? = usuarios.filterNotNull()
        .find { it.correo == normalizar(correo) && it.clave == resumen(clave) }

    fun recuperar(correo: String, pin: String, nuevaClave: String): String? {
        if (nuevaClave.length < 6) return "La contraseña debe tener al menos 6 caracteres."
        val posicion = usuarios.indexOfFirst { it?.correo == normalizar(correo) && it.pin == resumen(pin) }
        if (posicion == -1) return "El correo o el PIN no coinciden."
        usuarios[posicion] = usuarios[posicion]!!.copy(clave = resumen(nuevaClave))
        guardar()
        return null
    }

    private fun guardar() {
        val datos = JSONArray()
        usuarios.filterNotNull().forEach { usuario ->
            datos.put(JSONObject().put("nombre", usuario.nombre).put("correo", usuario.correo)
                .put("clave", usuario.clave).put("pin", usuario.pin))
        }
        preferencias.edit().putString("usuarios", datos.toString()).apply()
    }
}
