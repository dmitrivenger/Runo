package com.dmitrivenger.runo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary              = Brand_Forest,
    onPrimary            = Brand_OnForest,
    primaryContainer     = Brand_ForestVariant,
    onPrimaryContainer   = Dark_OnBackground,
    secondary            = Brand_Blue,
    onSecondary          = Dark_OnBackground,
    tertiary             = Brand_Red,
    onTertiary           = Dark_OnBackground,
    background           = Dark_Background,
    onBackground         = Dark_OnBackground,
    surface              = Dark_Surface,
    onSurface            = Dark_OnSurface,
    surfaceVariant       = Dark_SurfaceVariant,
    onSurfaceVariant     = Dark_OnSurfaceMuted,
    outline              = Dark_Border,
    error                = Brand_Red,
    onError              = Dark_OnBackground,
)

private val LightColorScheme = lightColorScheme(
    primary              = Brand_Forest,
    onPrimary            = Brand_OnForest,
    primaryContainer     = Brand_ForestVariant,
    onPrimaryContainer   = Light_OnBackground,
    secondary            = Brand_Blue,
    onSecondary          = Light_OnBackground,
    tertiary             = Brand_Red,
    onTertiary           = Light_OnBackground,
    background           = Light_Background,
    onBackground         = Light_OnBackground,
    surface              = Light_Surface,
    onSurface            = Light_OnSurface,
    surfaceVariant       = Light_SurfaceVariant,
    onSurfaceVariant     = Light_OnSurfaceMuted,
    outline              = Light_Border,
    error                = Brand_Red,
    onError              = Light_Surface,
)

@Composable
fun RunoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
