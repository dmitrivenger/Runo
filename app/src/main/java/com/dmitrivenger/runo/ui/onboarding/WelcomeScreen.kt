package com.dmitrivenger.runo.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmitrivenger.runo.ui.components.RunoPrimaryButton
import com.dmitrivenger.runo.ui.theme.Dark_Background
import com.dmitrivenger.runo.ui.theme.Dark_OnSurfaceMuted
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    val heroAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800),
        label = "heroAlpha",
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700, delayMillis = 400),
        label = "contentAlpha",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Dark_Background),
    ) {
        // ── Hero section (top 58%) ────────────────────────────────────────────
        AtmosphericHero(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.58f)
                .alpha(heroAlpha),
        )

        // ── Logo mark ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp)
                .alpha(heroAlpha),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "R",
                        style = MaterialTheme.typography.displaySmall.copy(letterSpacing = (-1).sp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "RUNO",
                    style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 6.sp),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        // ── Bottom content card ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .alpha(contentAlpha)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Welcome to Runo",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Track, understand, and improve\nyour running — simply.",
                style = MaterialTheme.typography.bodyLarge,
                color = Dark_OnSurfaceMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(40.dp))
            RunoPrimaryButton(
                text = "Let's get started",
                onClick = onGetStarted,
            )
        }
    }
}

@Composable
private fun AtmosphericHero(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Sky — deep dark gradient top to mid
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0D0D0D), Color(0xFF111A14)),
                startY = 0f,
                endY = h,
            ),
        )

        // Subtle ambient glow — moonlight/sun behind clouds
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x1522C55E),
                    Color(0x0022C55E),
                    Color.Transparent,
                ),
                center = Offset(w * 0.65f, h * 0.28f),
                radius = h * 0.45f,
            ),
            radius = h * 0.45f,
            center = Offset(w * 0.65f, h * 0.28f),
        )

        // Far hills — dark silhouette
        val farHills = Path().apply {
            moveTo(0f, h * 0.82f)
            cubicTo(w * 0.15f, h * 0.62f, w * 0.35f, h * 0.58f, w * 0.5f, h * 0.65f)
            cubicTo(w * 0.65f, h * 0.72f, w * 0.80f, h * 0.55f, w, h * 0.63f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = farHills,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0F1F14), Color(0xFF0A1510)),
                startY = h * 0.55f,
                endY = h,
            ),
        )

        // Near hills — slightly lighter, more detail
        val nearHills = Path().apply {
            moveTo(0f, h)
            lineTo(0f, h * 0.88f)
            cubicTo(w * 0.1f, h * 0.76f, w * 0.25f, h * 0.72f, w * 0.4f, h * 0.78f)
            cubicTo(w * 0.55f, h * 0.84f, w * 0.7f, h * 0.70f, w * 0.85f, h * 0.75f)
            cubicTo(w * 0.92f, h * 0.78f, w * 0.96f, h * 0.80f, w, h * 0.82f)
            lineTo(w, h)
            close()
        }
        drawPath(
            path = nearHills,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF142A1C), Color(0xFF0D1F13)),
                startY = h * 0.7f,
                endY = h,
            ),
        )

        // Trail path — subtle bright line through hills
        val trail = Path().apply {
            moveTo(w * 0.48f, h)
            cubicTo(w * 0.49f, h * 0.88f, w * 0.52f, h * 0.82f, w * 0.56f, h * 0.76f)
            cubicTo(w * 0.60f, h * 0.70f, w * 0.63f, h * 0.65f, w * 0.65f, h * 0.62f)
        }
        drawPath(
            path = trail,
            brush = Brush.linearGradient(
                colors = listOf(Color(0x4022C55E), Color(0x0022C55E)),
                start = Offset(w * 0.48f, h),
                end = Offset(w * 0.65f, h * 0.62f),
            ),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()),
        )

        // Bottom fade — scrim blending into the content card below
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0xFF0D0D0D)),
                startY = h * 0.55f,
                endY = h,
            ),
        )
    }
}
