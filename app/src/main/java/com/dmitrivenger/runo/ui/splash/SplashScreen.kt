package com.dmitrivenger.runo.ui.splash

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmitrivenger.runo.ui.theme.Dark_Background
import com.dmitrivenger.runo.ui.theme.Dark_OnSurfaceMuted
import kotlinx.coroutines.delay

// Custom easing — gentle overshoot on logo entrance
private val EaseOutBack = Easing { t ->
    val c1 = 1.70158f
    val c3 = c1 + 1f
    (1 + c3 * Math.pow((t - 1).toDouble(), 3.0) + c1 * Math.pow((t - 1).toDouble(), 2.0)).toFloat()
}

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    var triggered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        triggered = true
        delay(2000)
        onComplete()
    }

    val logoScale by animateFloatAsState(
        targetValue = if (triggered) 1f else 0.6f,
        animationSpec = tween(durationMillis = 700, easing = EaseOutBack),
        label = "logoScale",
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "logoAlpha",
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 300),
        label = "textAlpha",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Dark_Background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo mark — "R" in a rounded green box
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "R",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFeatureSettings = "tnum",
                        letterSpacing = (-1).sp,
                    ),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            Spacer(Modifier.height(20.dp))

            // Wordmark
            Text(
                text = "RUNO",
                style = MaterialTheme.typography.displayMedium.copy(letterSpacing = 8.sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(logoAlpha),
            )

            Spacer(Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Every step counts\ntoward something bigger.",
                style = MaterialTheme.typography.bodyLarge,
                color = Dark_OnSurfaceMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha)
                    .padding(horizontal = 48.dp),
            )
        }
    }
}
