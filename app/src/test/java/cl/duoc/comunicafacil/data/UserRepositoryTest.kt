package cl.duoc.comunicafacil.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositorioUsuariosTest {
    @Test
    fun guardaCincoUsuariosEnElArreglo() {
        val repositorio = RepositorioUsuarios(cargarDemo = false)
        repeat(RepositorioUsuarios.MAX_USUARIOS) {
            assertEquals(ResultadoRegistro.Exito, repositorio.registrar(usuario(it)))
        }
        assertEquals(5, repositorio.usuarios().size)
        assertEquals(0, repositorio.cupos())
        assertEquals(ResultadoRegistro.SinCupos, repositorio.registrar(usuario(9)))
    }

    @Test
    fun validaCorreoYClave() {
        val repositorio = RepositorioUsuarios(cargarDemo = false)
        val usuario = usuario(1)
        repositorio.registrar(usuario)
        assertNotNull(repositorio.ingresar(usuario.correo.uppercase(), usuario.clave))
        assertNull(repositorio.ingresar(usuario.correo, "incorrecta"))
    }

    @Test
    fun rechazaCorreoRepetido() {
        val repositorio = RepositorioUsuarios(cargarDemo = false)
        repositorio.registrar(usuario(1))
        val repetido = usuario(2).copy(correo = "USUARIO1@EJEMPLO.CL")
        assertEquals(ResultadoRegistro.CorreoRepetido, repositorio.registrar(repetido))
        assertEquals(1, repositorio.usuarios().size)
    }

    @Test
    fun rechazaClaveCorta() {
        val repositorio = RepositorioUsuarios(cargarDemo = false)
        assertEquals(
            ResultadoRegistro.DatosInvalidos,
            repositorio.registrar(usuario(1).copy(clave = "123")),
        )
        assertTrue(repositorio.usuarios().isEmpty())
        assertEquals(5, repositorio.cupos())
    }

    private fun usuario(numero: Int) = Usuario(
        "Usuario $numero",
        "usuario$numero@ejemplo.cl",
        "Clave${numero}Segura",
        "Texto y voz",
    )
}
