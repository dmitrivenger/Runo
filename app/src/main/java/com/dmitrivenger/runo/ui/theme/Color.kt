package com.dmitrivenger.runo.ui.theme

import androidx.compose.ui.graphics.Color

// ── Light mode ──────────────────────────────────────────────────────────────
val Light_BackgroundCream   = Color(0xFFF5EFD8)   // Warm cream — dominant background
val Light_CardSurface       = Color(0xFFFBF7E8)   // Slightly lighter cream for elevated cards
val Light_CardWhite         = Color(0xFFFFFFFF)   // Input fields and list rows
val Light_SecondaryText     = Color(0xFF3A4A3F)   // Warm dark gray for body text
val Light_MutedGray         = Color(0xFF8A8A7E)   // "View all", inactive labels, status text

// ── Dark mode ───────────────────────────────────────────────────────────────
// Dark_Background and Dark_OnSurfaceMuted are also referenced directly by
// SplashScreen and WelcomeScreen, which always render dark regardless of theme.
val Dark_Background         = Color(0xFF000000)   // Pure black — Active Run + Splash/Welcome
val Dark_CardSurface        = Color(0xFF1A1A1A)   // Slightly elevated dark for cards
val Dark_MapBase            = Color(0xFF1F1F1F)   // Map background on Active Run
val Dark_MapRoads           = Color(0xFF2E2E2E)   // Subtle road network
val Dark_OnSurfaceMuted     = Color(0xB3FFFFFF)   // White @ 70% — secondary text on dark
val Dark_ButtonBorder       = Color(0x4DFFFFFF)   // White @ 30% — outlined button borders

// ── Brand colours ───────────────────────────────────────────────────────────
val Brand_DeepGreen         = Color(0xFF0F3D2E)   // Primary — buttons, headlines, icons
val Brand_LeafGreen         = Color(0xFF5C9A4A)   // Bright accent — highlights, route lines, charts
val Brand_SoftGreenTint     = Color(0xFFA8C290)   // Chart area-fills, subtle highlights
val Brand_White             = Color(0xFFFFFFFF)   // Text on dark or brand-colored surfaces
val Brand_DangerRed         = Color(0xFFC0392B)   // Destructive actions only ("Log Out")
