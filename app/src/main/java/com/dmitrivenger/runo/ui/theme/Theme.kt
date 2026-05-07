package com.dmitrivenger.runo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light mode — cream backgrounds, deep green text and buttons
private val LightColorScheme = lightColorScheme(
    primary              = Brand_DeepGreen,       // #0F3D2E — filled buttons, active nav
    onPrimary            = Brand_White,            // Text/icons on green buttons
    primaryContainer     = Brand_SoftGreenTint,   // #A8C290 — chip/badge backgrounds
    onPrimaryContainer   = Brand_DeepGreen,
    secondary            = Brand_LeafGreen,        // #5C9A4A — accent, toggles ON
    onSecondary          = Brand_White,
    secondaryContainer   = Brand_SoftGreenTint,
    onSecondaryContainer = Brand_DeepGreen,
    tertiary             = Brand_SoftGreenTint,   // Subtle highlight tint
    onTertiary           = Brand_DeepGreen,
    background           = Light_BackgroundCream, // #F5EFD8 — warm cream
    onBackground         = Brand_DeepGreen,       // All primary text is deep green, not black
    surface              = Light_CardSurface,     // #FBF7E8 — elevated card cream
    onSurface            = Brand_DeepGreen,
    surfaceVariant       = Light_CardWhite,       // #FFFFFF — pure white input fields/rows
    onSurfaceVariant     = Light_SecondaryText,   // #3A4A3F — body text, subtitles
    outline              = Light_MutedGray,       // #8A8A7E — borders, inactive labels
    error                = Brand_DangerRed,       // #C0392B — destructive actions only
    onError              = Brand_White,
)

// Dark mode — pure black, leaf green as primary action color
private val DarkColorScheme = darkColorScheme(
    primary              = Brand_LeafGreen,       // #5C9A4A — leaf green becomes primary in dark
    onPrimary            = Brand_White,
    primaryContainer     = Brand_DeepGreen,       // #0F3D2E — muted container
    onPrimaryContainer   = Brand_LeafGreen,
    secondary            = Brand_LeafGreen,
    onSecondary          = Brand_White,
    secondaryContainer   = Dark_CardSurface,
    onSecondaryContainer = Brand_LeafGreen,
    tertiary             = Brand_SoftGreenTint,
    onTertiary           = Dark_Background,
    background           = Dark_Background,       // #000000 — pure black
    onBackground         = Brand_White,           // White text on dark background
    surface              = Dark_CardSurface,      // #1A1A1A — elevated cards
    onSurface            = Brand_White,
    surfaceVariant       = Dark_MapBase,          // #1F1F1F — map background
    onSurfaceVariant     = Dark_OnSurfaceMuted,   // White @ 70% — secondary text
    outline              = Dark_ButtonBorder,     // White @ 30% — button outlines
    error                = Brand_DangerRed,
    onError              = Brand_White,
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
