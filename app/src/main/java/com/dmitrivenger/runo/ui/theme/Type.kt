package com.dmitrivenger.runo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dmitrivenger.runo.R

// Satoshi is the design-spec primary; Inter is the bundled fallback.
// The font family name is kept as-is — swap the font files when Satoshi is available.
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular,  FontWeight.Normal),
    Font(R.font.inter_medium,   FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold,     FontWeight.Bold),
)

// Type scale — matches DESIGN_SYSTEM.md exactly.
// Slot → Design label → Used for
val Typography = Typography(

    // Display — 80–96sp, Bold, -2 tracking
    // Used for hero numbers: distance "5.21", duration on Active Run
    displayLarge = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 96.sp,
        lineHeight   = 96.sp,
        letterSpacing = (-2).sp,
    ),
    displayMedium = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 88.sp,
        lineHeight   = 88.sp,
        letterSpacing = (-2).sp,
    ),
    displaySmall = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 80.sp,
        lineHeight   = 80.sp,
        letterSpacing = (-2).sp,
    ),

    // H1 — 36–44sp, Bold, -1 tracking
    // Used for screen headlines: "Welcome to Runo", "Settings", "Analytics"
    headlineLarge = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 44.sp,
        lineHeight   = 52.sp,
        letterSpacing = (-1).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 40.sp,
        lineHeight   = 48.sp,
        letterSpacing = (-1).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 36.sp,
        lineHeight   = 44.sp,
        letterSpacing = (-1).sp,
    ),

    // H2 / Title — 18–28sp, SemiBold
    // titleLarge → H2 section headers: "Recent Runs", "Splits", "Your Stats"
    // titleMedium/Small → list labels, card titles
    titleLarge = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 28.sp,
        lineHeight   = 36.sp,
        letterSpacing = (-0.5).sp,
    ),
    titleMedium = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 20.sp,
        lineHeight   = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 18.sp,
        lineHeight   = 26.sp,
        letterSpacing = 0.sp,
    ),

    // Body — 15–16sp, Regular, 0 tracking
    // Paragraph text, subtitles, descriptions
    bodyLarge = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 16.sp,
        lineHeight   = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 15.sp,
        lineHeight   = 22.sp,
        letterSpacing = 0.sp,
    ),
    bodySmall = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.sp,
    ),

    // Label — 13–14sp, Medium, 0 tracking
    // Small inline labels: "Distance", "Time", stat values like "Metric"
    labelLarge = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.sp,
    ),
    labelMedium = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 13.sp,
        lineHeight   = 18.sp,
        letterSpacing = 0.sp,
    ),

    // Caption — 11–12sp, Regular, +0.5 tracking
    // Date stamps, tertiary info, footer text
    labelSmall = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
