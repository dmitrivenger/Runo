package com.dmitrivenger.runo.ui.run

import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun CountdownScreen(onCountdownComplete: () -> Unit) {
    val context = LocalContext.current
    var count by remember { mutableIntStateOf(5) }
    var showGo by remember { mutableStateOf(false) }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val t = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }
        tts = t
        onDispose { t.shutdown() }
    }

    val vibrator = remember {
        context.getSystemService(Vibrator::class.java)
    }

    LaunchedEffect(Unit) {
        repeat(5) { i ->
            val current = 5 - i
            count = current
            vibrator?.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            delay(1000)
        }
        showGo = true
        tts?.speak("Let's run", TextToSpeech.QUEUE_FLUSH, null, null)
        vibrator?.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        delay(800)
        onCountdownComplete()
    }

    val bgColor by animateColorAsState(
        targetValue = if (showGo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        animationSpec = tween(400),
        label = "bg"
    )
    val scale by animateFloatAsState(
        targetValue = if (showGo) 1.2f else 1f,
        animationSpec = tween(300),
        label = "scale"
    )

    Box(
        modifier = Modifier.fillMaxSize().background(bgColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (showGo) "GO" else count.toString(),
            style = MaterialTheme.typography.displayLarge,
            color = if (showGo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.scale(scale),
        )
    }
}
