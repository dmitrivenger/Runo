package com.dmitrivenger.runo.ui.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.dmitrivenger.runo.ui.theme.Brand_DeepGreen
import com.dmitrivenger.runo.ui.theme.Brand_White
import com.dmitrivenger.runo.ui.theme.Light_BackgroundCream
import com.dmitrivenger.runo.ui.theme.Light_MutedGray
import java.util.Calendar

// ── Mock data (replaced by Room DB in a later prompt) ────────────────────────
private data class MockRun(
    val date: String,
    val distanceText: String,
    val paceText: String,
    val sparkPoints: List<Float>,   // normalised 0..1, index 0 = run start
)

private val mockRuns = listOf(
    MockRun("May 14", "5.21 km", "6'24\"/km", listOf(0.50f, 0.65f, 0.42f, 0.72f, 0.55f, 0.63f, 0.48f)),
    MockRun("May 12", "6.15 km", "6'15\"/km", listOf(0.35f, 0.52f, 0.70f, 0.45f, 0.68f, 0.50f, 0.72f)),
    MockRun("May 10", "4.8 km",  "6'40\"/km", listOf(0.62f, 0.44f, 0.72f, 0.50f, 0.40f, 0.65f, 0.52f)),
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    onStartRun: () -> Unit,
    onRunClick: (Long) -> Unit,
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    var showRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            onStartRun()
        } else {
            showRationale = true
        }
    }

    fun handleStartRun() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED
        if (granted) {
            onStartRun()
        } else {
            val perms = buildList {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    add(Manifest.permission.POST_NOTIFICATIONS)
            }.toTypedArray()
            permissionLauncher.launch(perms)
        }
    }

    // Full-screen cream background (fills behind status bar in edge-to-edge mode)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),    // push content below transparent status bar
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp,
                bottom = 8.dp,          // Scaffold's innerPadding handles nav-bar clearance
            ),
        ) {
            // ── Greeting ─────────────────────────────────────────────────────
            item {
                GreetingRow(
                    name = if (profile.name.isNotBlank()) profile.name else "Runner",
                )
            }

            item { Spacer(Modifier.height(32.dp)) }

            // ── Hero card ─────────────────────────────────────────────────────
            item {
                HeroCard(onTap = { handleStartRun() })
            }

            item { Spacer(Modifier.height(32.dp)) }

            // ── Recent Runs header ────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = "Recent Runs",
                        style = MaterialTheme.typography.titleLarge,    // 28sp SemiBold
                        color = Brand_DeepGreen,
                    )
                    Text(
                        text = "View all",
                        style = MaterialTheme.typography.labelLarge,    // 14sp Medium
                        color = Light_MutedGray,
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // ── Run cards ─────────────────────────────────────────────────────
            // All three in a Column so they share one LazyColumn item — avoids
            // the items() import and keeps spacing simple.
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    mockRuns.forEach { run -> RunCard(run = run) }
                }
            }
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Location needed") },
            text = { Text("Runo needs location access to track your run. Please grant it in your device Settings.") },
            confirmButton = {
                TextButton(onClick = { showRationale = false }) { Text("OK") }
            },
        )
    }
}

// ── Greeting row ──────────────────────────────────────────────────────────────
@Composable
private fun GreetingRow(name: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = buildGreeting(),
                style = MaterialTheme.typography.bodyLarge,     // 16sp Regular
                color = Light_MutedGray,
            )
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall, // 36sp Bold
                color = Brand_DeepGreen,
            )
        }

        // Avatar — circle with user's initial; replace with photo picker later
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brand_DeepGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.take(1).uppercase().ifBlank { "R" },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Brand_DeepGreen,
            )
        }
    }
}

// ── Hero card ─────────────────────────────────────────────────────────────────
@Composable
private fun HeroCard(onTap: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onTap() },
        shape = RoundedCornerShape(24.dp),
        color = Brand_DeepGreen,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Left: text
            Column {
                Text(
                    text = "Ready to run?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                    ),
                    color = Brand_White,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Start your next adventure.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Brand_White.copy(alpha = 0.8f),
                )
            }

            // Right: white play circle with deep green triangle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Start run",
                    tint = Brand_DeepGreen,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}

// ── Run card ──────────────────────────────────────────────────────────────────
@Composable
private fun RunCard(run: MockRun) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left: date above distance
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = run.date,
                    style = MaterialTheme.typography.labelMedium,   // 13sp Medium
                    color = Light_MutedGray,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = run.distanceText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    ),
                    color = Brand_DeepGreen,
                )
            }

            // Middle: pace
            Text(
                text = run.paceText,
                style = MaterialTheme.typography.labelLarge,        // 14sp Medium
                color = Brand_DeepGreen,
            )

            Spacer(Modifier.width(16.dp))

            // Right: sparkline
            SparklineGraph(
                points = run.sparkPoints,
                modifier = Modifier
                    .width(96.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
        }
    }
}

// ── Sparkline graph ───────────────────────────────────────────────────────────
// Draws a smooth bezier curve with a 15%-opacity filled area underneath.
// Background is the brand cream so it blends into the white card.
@Composable
private fun SparklineGraph(points: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Light_BackgroundCream)) {
        if (points.size < 2) return@Canvas

        val padH = 8.dp.toPx()
        val padV = 8.dp.toPx()
        val drawW = size.width - padH * 2
        val drawH = size.height - padV * 2

        // Map each point to pixel coordinates.
        // Y is inverted: higher float value = higher on screen.
        val xs = points.indices.map { i -> padH + i.toFloat() / (points.lastIndex) * drawW }
        val ys = points.map { p -> padV + (1f - p) * drawH }

        // Smooth cubic bezier — control points pulled toward the midpoint X
        val linePath = Path().apply {
            moveTo(xs[0], ys[0])
            for (i in 1 until points.size) {
                val cpX = (xs[i - 1] + xs[i]) / 2f
                cubicTo(cpX, ys[i - 1], cpX, ys[i], xs[i], ys[i])
            }
        }

        // Area fill — 15% opacity leaf green
        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(xs.last(), size.height)
            lineTo(xs.first(), size.height)
            close()
        }
        drawPath(fillPath, color = Color(0xFF5C9A4A).copy(alpha = 0.15f))

        // Line — 2dp leaf green
        drawPath(
            path = linePath,
            color = Color(0xFF5C9A4A),
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────
private fun buildGreeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 5..11  -> "Good morning,"
    in 12..17 -> "Good afternoon,"
    else       -> "Good evening,"
}
