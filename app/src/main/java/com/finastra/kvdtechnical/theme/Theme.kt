package com.finastra.kvdtechnical.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkColorPalette = darkColorScheme(
    primary = Magenta400,
    background = Purple900,
    surface = Purple800,
    surfaceTint = Magenta700,
    surfaceBright = Magenta700





)

private val LightColorPalette = lightColorScheme(
    primary = Magenta900,
    surfaceTint = Color.Transparent,
    surface = Color.White,
    surfaceBright = Purple700


    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun TechProjKarlDiomaroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit) {

    val colorScheme = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
//        DarkColorPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}