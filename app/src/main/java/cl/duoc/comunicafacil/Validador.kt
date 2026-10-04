package cl.duoc.comunicafacil

import java.util.Locale

object Validador {
    fun correo(valor: String) = valor.trim().lowercase(Locale.ROOT)
    fun registro(nombre: String, correo: String, clave: String, pin: String): String? = when {
        nombre.trim().length < 2 -> "Escribe un nombre de al menos 2 letras."
        !correo.trim().matches(Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) -> "Escribe un correo válido."
        clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
        !pin.matches(Regex("[0-9]{4}")) -> "El PIN debe tener 4 números."
        else -> null
    }
    fun mensaje(texto: String): String? = when {
        texto.isBlank() -> "Escribe un mensaje primero."
        texto.trim().length > 1000 -> "El mensaje no puede superar 1000 caracteres."
        else -> null
    }
}

data class Mensaje(val id: String, val texto: String, val creado: Long)
data class Cuenta(val uid: String, val nombre: String, val correo: String)

interface Backend {
    val remoto: Boolean
    fun actual(): Cuenta?
    fun registrar(nombre: String, correo: String, clave: String, pin: String, fin: (String?) -> Unit)
    fun ingresar(correo: String, clave: String, fin: (String?) -> Unit)
    fun recuperar(correo: String, pin: String, clave: String, fin: (String?) -> Unit)
    fun salir()
    fun consultar(fin: (List<Mensaje>?, String?) -> Unit)
    fun crear(texto: String, fin: (String?) -> Unit)
    fun modificar(id: String, texto: String, fin: (String?) -> Unit)
    fun eliminar(id: String, fin: (String?) -> Unit)
    fun borrar(fin: (String?) -> Unit)
}
