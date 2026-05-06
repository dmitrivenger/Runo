package com.dmitrivenger.runo.ui.run

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.domain.model.UserProfile
import com.dmitrivenger.runo.ui.components.MapLibreMapView
import com.dmitrivenger.runo.ui.components.buildRouteGeoJson
import com.dmitrivenger.runo.ui.components.toMapLibre
import kotlinx.coroutines.launch
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource

private const val ROUTE_SOURCE_ID = "route-source"
private const val ROUTE_LAYER_ID = "route-layer"
private const val ROUTE_COLOR = "#1DB954"

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

    LaunchedEffect(routeSource, state.routePoints) {
        val src = routeSource ?: return@LaunchedEffect
        src.setGeoJson(buildRouteGeoJson(state.routePoints))
        state.routePoints.lastOrNull()?.let { last ->
            mapController?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(last.toMapLibre(), 16.0), 800
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapLibreMapView(
            modifier = Modifier.fillMaxSize(),
            onMapReady = { map, style ->
                setupRouteLayer(style) { src -> routeSource = src }
                mapController = map
            }
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                .padding(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MetricDisplay("DISTANCE", "%.2f".format(state.distanceMeters / 1000f), "km")
                MetricDisplay("PACE", formatPace(state.currentPaceSecondsPerKm), "/km")
                MetricDisplay("TIME", formatTime(state.elapsedSeconds), "")
            }
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { showEndDialog = true },
                    modifier = Modifier.size(56.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                ) {
                    Icon(Icons.Default.Stop, "End Run",
                        tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(32.dp))
                IconButton(
                    onClick = { viewModel.togglePause() },
                    modifier = Modifier.size(72.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                ) {
                    Icon(
                        imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (state.isPaused) "Resume" else "Pause",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (state.isPaused) {
            Box(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Text("PAUSED", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary)
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                ) { Text("End Run") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDialog = false }) { Text("Keep going") }
            }
        )
    }
}

private fun setupRouteLayer(style: Style, onSourceReady: (GeoJsonSource) -> Unit) {
    val source = GeoJsonSource(ROUTE_SOURCE_ID, """{"type":"FeatureCollection","features":[]}""")
    style.addSource(source)
    style.addLayer(
        LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID).withProperties(
            PropertyFactory.lineColor(ROUTE_COLOR),
            PropertyFactory.lineWidth(10f),
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
        )
    )
    onSourceReady(source)
}

@Composable
private fun MetricDisplay(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface)
        if (unit.isNotEmpty()) {
            Text(unit, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun formatPace(paceSeconds: Float): String {
    if (paceSeconds <= 0f) return "--:--"
    return "%d:%02d".format((paceSeconds / 60).toInt(), (paceSeconds % 60).toInt())
}

private fun formatTime(seconds: Long): String {
    val h = seconds / 3600; val m = (seconds % 3600) / 60; val s = seconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}
