package com.dmitrivenger.runo.ui.splash

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmitrivenger.runo.R
import com.dmitrivenger.runo.ui.theme.Brand_DeepGreen
import com.dmitrivenger.runo.ui.theme.Light_MutedGray
import kotlinx.coroutines.delay

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
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        // ── Centre content ────────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo mark + warm ambient glow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha),
            ) {
                // Soft warm glow ring (cream-gold, matches reference)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFDAB96A).copy(alpha = 0.32f),
                                    Color.Transparent,
                                )
                            ),
                            shape = CircleShape,
                        )
                )
                // R glyph + motion lines (deep green, no background tile)
                Icon(
                    painter = painterResource(R.drawable.runo_logo),
                    contentDescription = null,
                    tint = Brand_DeepGreen,
                    modifier = Modifier.size(108.dp),
                )
            }

            Spacer(Modifier.height(12.dp))

            // "RUNO" wordmark
            Text(
                text = "RUNO",
                style = MaterialTheme.typography.displayMedium.copy(letterSpacing = 8.sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(logoAlpha),
            )

            Spacer(Modifier.height(12.dp))

            // Tagline
            Text(
                text = "Every step counts toward\nsomething bigger",
                style = MaterialTheme.typography.bodyLarge,
                color = Light_MutedGray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha)
                    .padding(horizontal = 48.dp),
            )
        }

        // ── Bottom: — • — divider + three loading dots ────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Decorative — • — rule
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 96.dp)
                    .alpha(textAlpha),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Brand_DeepGreen.copy(alpha = 0.25f))
                )
                Box(
                    Modifier
                        .padding(horizontal = 10.dp)
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Brand_DeepGreen.copy(alpha = 0.50f))
                )
                Box(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Brand_DeepGreen.copy(alpha = 0.25f))
                )
            }

            // Three dots
            Row(
                modifier = Modifier.alpha(textAlpha),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Brand_DeepGreen)
                )
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Brand_DeepGreen.copy(alpha = 0.45f))
                )
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Brand_DeepGreen.copy(alpha = 0.45f))
                )
            }
        }
    }
}
