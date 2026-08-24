package cl.duoc.comunicafacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.comunicafacil.ui.AplicacionComunicaFacil
import cl.duoc.comunicafacil.ui.theme.ComunicaFacilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComunicaFacilTheme {
                AplicacionComunicaFacil()
            }
        }
    }
}
