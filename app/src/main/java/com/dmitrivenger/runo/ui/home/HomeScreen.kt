package com.dmitrivenger.runo.ui.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.dmitrivenger.runo.R
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.ui.components.MiniRoutePreview
import com.dmitrivenger.runo.ui.theme.Brand_DeepGreen
import com.dmitrivenger.runo.ui.theme.Brand_LeafGreen
import com.dmitrivenger.runo.ui.theme.Brand_White
import com.dmitrivenger.runo.ui.theme.Light_MutedGray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    onStartRun: () -> Unit,
    onRunClick: (Long) -> Unit,
    onDeleteRun: (Long) -> Unit,
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val runs by viewModel.runs.collectAsState()
    var showRationale by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp,
                bottom = 8.dp,
            ),
        ) {
            item {
                GreetingRow(name = if (profile.name.isNotBlank()) profile.name else stringResource(R.string.default_name))
            }

            item { Spacer(Modifier.height(32.dp)) }

            item {
                HeroCard(onTap = { handleStartRun() })
            }

            item { Spacer(Modifier.height(32.dp)) }

            item {
                Text(
                    text = stringResource(R.string.recent_runs),
                    style = MaterialTheme.typography.titleLarge,
                    color = Brand_DeepGreen,
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            if (runs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.runs_placeholder),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Light_MutedGray,
                        )
                    }
                }
            } else {
                items(runs, key = { it.id }) { run ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                pendingDeleteId = run.id
                            }
                            false // always snap back; deletion happens via dialog
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 12.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFEF4444)),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier.padding(end = 24.dp),
                                )
                            }
                        },
                    ) {
                        RunCard(run = run, onClick = { onRunClick(run.id) })
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text(stringResource(R.string.location_needed)) },
            text = { Text(stringResource(R.string.location_rationale)) },
            confirmButton = {
                TextButton(onClick = { showRationale = false }) { Text(stringResource(R.string.ok)) }
            },
        )
    }

    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text(stringResource(R.string.delete_run_title)) },
            text = { Text(stringResource(R.string.delete_run_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDeleteId?.let { onDeleteRun(it) }
                        pendingDeleteId = null
                    },
                ) { Text(stringResource(R.string.delete), color = Color(0xFFEF4444)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

// ── Greeting row (no avatar) ──────────────────────────────────────────────────
@Composable
private fun GreetingRow(name: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = buildGreeting(),
            style = MaterialTheme.typography.bodyLarge,
            color = Light_MutedGray,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            color = Brand_DeepGreen,
        )
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
            Column {
                Text(
                    text = stringResource(R.string.ready_to_run),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                    ),
                    color = Brand_White,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.start_adventure),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Brand_White.copy(alpha = 0.8f),
                )
            }
            PulsingPlayButton(onClick = onTap)
        }
    }
}

// ── Run card ──────────────────────────────────────────────────────────────────
@Composable
private fun RunCard(run: Run, onClick: () -> Unit) {
    val dateStr = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(run.startTime))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(onClick = onClick),
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelMedium,
                    color = Light_MutedGray,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "%.2f ${stringResource(R.string.unit_km)}".format(run.distanceKm),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    ),
                    color = Brand_DeepGreen,
                )
            }

            Text(
                text = run.formattedPace().replace(" /km", " ${stringResource(R.string.unit_per_km)}"),
                style = MaterialTheme.typography.labelLarge,
                color = Brand_DeepGreen,
            )

            Spacer(Modifier.width(16.dp))

            MiniRoutePreview(
                points = run.routePoints,
                modifier = Modifier
                    .width(96.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────
@Composable
private fun buildGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return stringResource(
        when (hour) {
            in 5..11  -> R.string.greeting_morning
            in 12..17 -> R.string.greeting_afternoon
            else       -> R.string.greeting_evening
        }
    )
}

// ── Pulsing play button ───────────────────────────────────────────────────────
@Composable
private fun PulsingPlayButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseRing")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulseProgress",
    )
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "pressScale",
    )

    Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val baseRadius = 34.dp.toPx()
            val expansionRadius = 14.dp.toPx()
            drawCircle(
                color = Brand_LeafGreen.copy(alpha = 0.4f * (1f - pulseProgress)),
                radius = baseRadius + pulseProgress * expansionRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
            )
        }
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Brand_LeafGreen.copy(alpha = 0.30f), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(64.dp)
                .scale(pressScale)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.start_run_cd),
                tint = Brand_DeepGreen,
                modifier = Modifier.size(36.dp),
            )
        }
    }
}
