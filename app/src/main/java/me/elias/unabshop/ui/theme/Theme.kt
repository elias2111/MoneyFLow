package me.elias.unabshop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Importar tus colores desde Color.kt
import me.elias.unabshop.ui.theme.Purple40
import me.elias.unabshop.ui.theme.Purple20
import me.elias.unabshop.ui.theme.PurpleGrey40
import me.elias.unabshop.ui.theme.Pink40

// Importar typographies desde Type.kt
import me.elias.unabshop.ui.theme.Typography

private val LightColors = lightColorScheme(
    primary = Purple40,
    onPrimary = Purple20,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun UnabShopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
