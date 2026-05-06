package com.dmitrivenger.runo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen,
    onPrimary = Color.Black,
    primaryContainer = DarkGreenVariant,
    onPrimaryContainer = Color.White,
    secondary = DarkBlue,
    onSecondary = Color.White,
    tertiary = DarkRed,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceMuted,
    outline = DarkOnSurfaceMuted,
)

private val LightColorScheme = lightColorScheme(
    primary = LightGreen,
    onPrimary = Color.White,
    primaryContainer = LightGreenVariant,
    onPrimaryContainer = Color.White,
    secondary = LightBlue,
    onSecondary = Color.White,
    tertiary = LightRed,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceMuted,
    outline = LightOnSurfaceMuted,
)

@Composable
fun RunoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
