package cl.duoc.comunicafacil

import org.junit.Assert.*
import org.junit.Test

class ValidadorTest {
    @Test fun normalizaCorreoSinCambiarDominio() { assertEquals("matias@ejemplo.cl", Validador.correo(" MATIAS@Ejemplo.cl ")) }
    @Test fun aceptaRegistroValido() { assertNull(Validador.registro("Matías", "matias@ejemplo.cl", "Clave123", "1234")) }
    @Test fun rechazaNombreCorto() { assertNotNull(Validador.registro("M", "matias@ejemplo.cl", "Clave123", "1234")) }
    @Test fun rechazaCorreoSinDominio() { assertNotNull(Validador.registro("Matías", "matias@", "Clave123", "1234")) }
    @Test fun rechazaCorreoConEspacios() { assertNotNull(Validador.registro("Matías", "ma tias@ejemplo.cl", "Clave123", "1234")) }
    @Test fun rechazaClaveCorta() { assertNotNull(Validador.registro("Matías", "matias@ejemplo.cl", "12345", "1234")) }
    @Test fun aceptaClaveDeSeisCaracteres() { assertNull(Validador.registro("Matías", "matias@ejemplo.cl", "123456", "1234")) }
    @Test fun rechazaPinConLetras() { assertNotNull(Validador.registro("Matías", "matias@ejemplo.cl", "Clave123", "12ab")) }
    @Test fun rechazaPinDeCincoDigitos() { assertNotNull(Validador.registro("Matías", "matias@ejemplo.cl", "Clave123", "12345")) }
    @Test fun rechazaMensajeVacio() { assertNotNull(Validador.mensaje("  \n ")) }
    @Test fun aceptaMensajeDeMilCaracteres() { assertNull(Validador.mensaje("a".repeat(1000))) }
    @Test fun rechazaMensajeMayorAlLimite() { assertNotNull(Validador.mensaje("a".repeat(1001))) }
}
