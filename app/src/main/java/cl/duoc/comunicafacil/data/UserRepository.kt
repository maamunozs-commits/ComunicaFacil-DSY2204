package cl.duoc.comunicafacil.data

data class Usuario(
    val nombre: String,
    val correo: String,
    val clave: String,
    val preferencia: String,
)

sealed interface ResultadoRegistro {
    data object Exito : ResultadoRegistro
    data object CorreoRepetido : ResultadoRegistro
    data object SinCupos : ResultadoRegistro
    data object DatosInvalidos : ResultadoRegistro
}

// Repositorio local solicitado en la actividad: un arreglo de cinco usuarios.
class RepositorioUsuarios(cargarDemo: Boolean = true) {
    private val datos = arrayOfNulls<Usuario>(MAX_USUARIOS)

    init {
        if (cargarDemo) {
            datos[0] = Usuario("Usuario Demo", CORREO_DEMO, CLAVE_DEMO, "Texto y voz")
        }
    }

    fun ingresar(correo: String, clave: String) = datos.filterNotNull().firstOrNull {
        it.correo.equals(correo.trim(), true) && it.clave == clave
    }

    fun registrar(usuario: Usuario): ResultadoRegistro {
        val nuevo = usuario.copy(
            nombre = usuario.nombre.trim(),
            correo = usuario.correo.trim().lowercase(),
        )
        if (nuevo.nombre.isBlank() || !nuevo.correo.contains("@") || nuevo.clave.length < 8) {
            return ResultadoRegistro.DatosInvalidos
        }
        if (buscar(nuevo.correo) != null) return ResultadoRegistro.CorreoRepetido
        val posicion = datos.indexOfFirst { it == null }
        if (posicion == -1) return ResultadoRegistro.SinCupos
        datos[posicion] = nuevo
        return ResultadoRegistro.Exito
    }

    fun buscar(correo: String) = datos.filterNotNull().firstOrNull {
        it.correo.equals(correo.trim(), true)
    }

    fun usuarios() = datos.filterNotNull()
    fun cupos() = datos.count { it == null }

    companion object {
        const val MAX_USUARIOS = 5
        const val CORREO_DEMO = "demo@comunicafacil.cl"
        const val CLAVE_DEMO = "Acceso123"
    }
}
