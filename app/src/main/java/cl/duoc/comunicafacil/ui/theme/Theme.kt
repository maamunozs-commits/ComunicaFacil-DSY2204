package cl.duoc.comunicafacil.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val LightColors = lightColorScheme(
    primary = Color(0xFF075985),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7F0FF),
    onPrimaryContainer = Color(0xFF002F45),
    secondary = Color(0xFF0F766E),
    onSecondary = Color.White,
    tertiary = Color(0xFF7C3AED),
    background = Color(0xFFF7FAFC),
    surface = Color.White,
    onSurface = Color(0xFF17202A),
    error = Color(0xFFB42318),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8BD3FF),
    onPrimary = Color(0xFF00344C),
    primaryContainer = Color(0xFF004C6C),
    secondary = Color(0xFF75DDD2),
    background = Color(0xFF0D151A),
    surface = Color(0xFF142029),
    onSurface = Color(0xFFE4EDF3),
)

@Composable
fun ComunicaFacilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colors.background.toArgb()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                window.navigationBarColor = colors.background.toArgb()
            }
        }
    }
    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
