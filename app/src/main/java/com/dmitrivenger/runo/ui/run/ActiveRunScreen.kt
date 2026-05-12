package com.dmitrivenger.runo.ui.run

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.domain.model.UserProfile
import com.dmitrivenger.runo.ui.components.MapLibreMapView
import com.dmitrivenger.runo.ui.components.buildPointGeoJson
import com.dmitrivenger.runo.ui.components.buildRouteGeoJson
import com.dmitrivenger.runo.ui.components.toMapLibre
import kotlinx.coroutines.launch
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource

private const val ROUTE_SOURCE_ID   = "route-source"
private const val ROUTE_LAYER_GLOW  = "route-layer-glow"
private const val ROUTE_LAYER_LINE  = "route-layer-line"
private const val TRAIL_SOURCE_ID   = "trail-source"
private const val TRAIL_LAYER_ID    = "trail-layer"
private const val POINTER_SOURCE_ID = "pointer-source"
private const val POINTER_LAYER_ID  = "pointer-layer"
private const val ROUTE_COLOR       = "#0F3D2E"   // brand deep green
private const val ROUTE_GLOW_COLOR  = "#A8C290"   // soft green tint glow
private const val TRAIL_COLOR       = "#4285F4"   // soft blue recent-trail
private const val TRAIL_POINTS      = 15

@Composable
fun ActiveRunScreen(
    viewModel: ActiveRunViewModel,
    userProfile: UserProfile,
    onRunFinished: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var showEndDialog by remember { mutableStateOf(false) }

    var mapController by remember { mutableStateOf<MapLibreMap?>(null) }
    var routeSource by remember { mutableStateOf<GeoJsonSource?>(null) }
    var trailSource by remember { mutableStateOf<GeoJsonSource?>(null) }
    var pointerSource by remember { mutableStateOf<GeoJsonSource?>(null) }

    LaunchedEffect(routeSource, trailSource, pointerSource, state.routePoints) {
        val rSrc = routeSource ?: return@LaunchedEffect
        rSrc.setGeoJson(buildRouteGeoJson(state.routePoints))
        val trailPoints = state.routePoints.takeLast(TRAIL_POINTS)
        trailSource?.setGeoJson(buildRouteGeoJson(trailPoints))
        state.routePoints.lastOrNull()?.let { last ->
            pointerSource?.setGeoJson(buildPointGeoJson(last))
            mapController?.easeCamera(
                CameraUpdateFactory.newLatLngZoom(last.toMapLibre(), 16.0), 500
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Full-screen map ───────────────────────────────────────────────────
        MapLibreMapView(
            modifier = Modifier.fillMaxSize(),
            onMapReady = { map, style ->
                setupRouteLayer(style) { rSrc, tSrc, pSrc ->
                    routeSource = rSrc
                    trailSource = tSrc
                    pointerSource = pSrc
                }
                mapController = map
            }
        )

        // ── PAUSED badge ──────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = state.isPaused,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFEF4444).copy(alpha = 0.92f))
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "PAUSED",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }
        }

        // ── Bottom sheet ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 20.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)),
            )

            // 2×2 metric grid
            val speedKmh = if (state.currentPaceSecondsPerKm > 0f) 3600f / state.currentPaceSecondsPerKm else 0f
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RunMetricCard(
                        label = "DISTANCE",
                        value = "%.2f".format(state.distanceMeters / 1000f),
                        unit = "km",
                        modifier = Modifier.weight(1f),
                    )
                    RunMetricCard(
                        label = "SPEED",
                        value = if (speedKmh > 0f) "%.1f".format(speedKmh) else "--.-",
                        unit = "km/h",
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RunMetricCard(
                        label = "TIME",
                        value = formatTime(state.elapsedSeconds),
                        unit = "",
                        modifier = Modifier.weight(1f),
                    )
                    RunMetricCard(
                        label = "CALORIES",
                        value = if (state.caloriesBurned > 0f) "${state.caloriesBurned.toInt()}" else "--",
                        unit = "kcal",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Controls — animate between running and paused layouts
            AnimatedContent(
                targetState = state.isPaused,
                transitionSpec = {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                },
                label = "controls",
            ) { isPaused ->
                if (isPaused) {
                    // Paused layout: Resume (primary) + End Run (destructive outline)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Button(
                            onClick = { viewModel.togglePause() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Resume", style = MaterialTheme.typography.titleMedium)
                        }
                        OutlinedButton(
                            onClick = { showEndDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(
                                    width = 1.5.dp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = RoundedCornerShape(16.dp),
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.tertiary,
                            ),
                            border = null,
                        ) {
                            Text("End Run", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                } else {
                    // Running layout: stop (left) + pause (centre)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = { showEndDialog = true },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)),
                        ) {
                            Icon(
                                Icons.Default.Stop,
                                contentDescription = "End run",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                        Spacer(Modifier.width(36.dp))
                        IconButton(
                            onClick = { viewModel.togglePause() },
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                        ) {
                            Icon(
                                Icons.Default.Pause,
                                contentDescription = "Pause",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(36.dp),
                            )
                        }
                        Spacer(Modifier.width(36.dp + 56.dp))
                    }
                }
            }
        }
    }

    if (showEndDialog) {
        AlertDialog(
            onDismissRequest = { showEndDialog = false },
            title = { Text("End this run?") },
            text = { Text("Your session will be saved and you'll see your results.") },
            confirmButton = {
                Button(
                    onClick = {
                        showEndDialog = false
                        scope.launch {
                            val id = viewModel.finishRun(userProfile)
                            onRunFinished(id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                    ),
                ) { Text("End Run") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDialog = false }) { Text("Keep going") }
            },
        )
    }
}

private fun setupRouteLayer(
    style: Style,
    onSourcesReady: (route: GeoJsonSource, trail: GeoJsonSource, pointer: GeoJsonSource) -> Unit,
) {
    val empty = """{"type":"FeatureCollection","features":[]}"""
    val routeSrc   = GeoJsonSource(ROUTE_SOURCE_ID, empty)
    val trailSrc   = GeoJsonSource(TRAIL_SOURCE_ID, empty)
    val pointerSrc = GeoJsonSource(POINTER_SOURCE_ID, empty)
    style.addSource(routeSrc)
    style.addSource(trailSrc)
    style.addSource(pointerSrc)

    // Outer glow — soft, wide halo matching brand tint
    style.addLayer(
        LineLayer(ROUTE_LAYER_GLOW, ROUTE_SOURCE_ID).withProperties(
            PropertyFactory.lineColor(ROUTE_GLOW_COLOR),
            PropertyFactory.lineWidth(20f),
            PropertyFactory.lineOpacity(0.35f),
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
        )
    )

    // Inner route line — brand deep green
    style.addLayer(
        LineLayer(ROUTE_LAYER_LINE, ROUTE_SOURCE_ID).withProperties(
            PropertyFactory.lineColor(ROUTE_COLOR),
            PropertyFactory.lineWidth(5f),
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
        )
    )

    // Blue trailing highlight — last N points
    style.addLayer(
        LineLayer(TRAIL_LAYER_ID, TRAIL_SOURCE_ID).withProperties(
            PropertyFactory.lineColor(TRAIL_COLOR),
            PropertyFactory.lineWidth(5f),
            PropertyFactory.lineOpacity(0.85f),
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
        )
    )

    // Current-position pointer circle
    style.addLayer(
        CircleLayer(POINTER_LAYER_ID, POINTER_SOURCE_ID).withProperties(
            PropertyFactory.circleColor(TRAIL_COLOR),
            PropertyFactory.circleRadius(10f),
            PropertyFactory.circleStrokeColor("#FFFFFF"),
            PropertyFactory.circleStrokeWidth(3f),
        )
    )

    onSourcesReady(routeSrc, trailSrc, pointerSrc)
}

@Composable
private fun RunMetricCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (unit.isNotEmpty()) {
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun formatPace(paceSeconds: Float): String {
    if (paceSeconds <= 0f) return "--:--"
    return "%d:%02d".format((paceSeconds / 60).toInt(), (paceSeconds % 60).toInt())
}

private fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}
