package com.example.ledscontroller.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkColorPalette = darkColorScheme(
    primary = Color.Black,
    background = DarkGrey,
    onBackground = Color.Black,
    surface = LightBlue,
    onSurface = DarkGrey
)

private val LightColorPalette = lightColorScheme(
    primary = Color.White,
    background = Color.White,
    surface = LightBlue,
    onSurface = Color.Yellow
)

@Composable
fun LEDsControllerAppTheme(darkTheme: Boolean = true, content: @Composable() () -> Unit) {
    val colors = when {
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }
    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}