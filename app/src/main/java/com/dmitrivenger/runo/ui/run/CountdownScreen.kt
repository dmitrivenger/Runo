package com.dmitrivenger.runo.ui.run

import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.R
import kotlinx.coroutines.delay
import java.util.Locale

private const val TOTAL_COUNT = 5

@Composable
fun CountdownScreen(onCountdownComplete: () -> Unit) {
    val context = LocalContext.current
    var count by remember { mutableIntStateOf(TOTAL_COUNT) }
    var showGo by remember { mutableStateOf(false) }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val t = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault()
        }
        tts = t
        onDispose { t.shutdown() }
    }

    val vibrator = remember { context.getSystemService(Vibrator::class.java) }
    val letsRunText = stringResource(R.string.lets_run)

    LaunchedEffect(Unit) {
        repeat(TOTAL_COUNT) { i ->
            count = TOTAL_COUNT - i
            vibrator?.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            delay(1000)
        }
        showGo = true
        tts?.speak(letsRunText, TextToSpeech.QUEUE_FLUSH, null, null)
        vibrator?.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        delay(800)
        onCountdownComplete()
    }

    // Background flashes green on "GO"
    val bgColor by animateColorAsState(
        targetValue = if (showGo) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.background,
        animationSpec = tween(350),
        label = "bg",
    )

    // Arc sweep: counts down from full circle to 0
    val arcSweep by animateFloatAsState(
        targetValue = if (showGo) 360f else (count.toFloat() / TOTAL_COUNT) * 360f,
        animationSpec = tween(300),
        label = "arc",
    )

    // Per-tick pop: new Animatable each time count changes, starts big → springs to 1f
    val popScale = remember(count) { Animatable(if (showGo) 1f else 1.35f) }
    LaunchedEffect(count) {
        if (!showGo) popScale.animateTo(1f, animationSpec = tween(500, easing = EaseOutBack))
    }

    val goScale = remember(showGo) { Animatable(if (showGo) 1.4f else 1f) }
    LaunchedEffect(showGo) {
        if (showGo) goScale.animateTo(1f, animationSpec = tween(400, easing = EaseOutBack))
    }

    val ringColor = MaterialTheme.colorScheme.primary
    val ringTrack = MaterialTheme.colorScheme.surfaceVariant
    val goLabel = stringResource(R.string.go_label)
    val getReadyLabel = stringResource(R.string.get_ready)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center,
    ) {
        if (!showGo) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val stroke = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                drawArc(
                    color = ringTrack,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = stroke,
                )
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = arcSweep,
                    useCenter = false,
                    style = stroke,
                )
            }
        }

        // Clip Box prevents scaled text from bleeding outside the ring boundary
        Box(
            modifier = Modifier
                .size(168.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(if (showGo) goScale.value else popScale.value),
            ) {
                Text(
                    text = if (showGo) goLabel else count.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = if (showGo) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onBackground,
                )
                if (!showGo) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = getReadyLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
