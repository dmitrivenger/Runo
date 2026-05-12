package com.dmitrivenger.runo.ui.history

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.domain.model.LatLng
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.ui.components.MapLibreMapView
import com.dmitrivenger.runo.ui.components.buildPointGeoJson
import com.dmitrivenger.runo.ui.components.buildRouteGeoJson
import com.dmitrivenger.runo.ui.components.toMapLibre
import com.dmitrivenger.runo.ui.theme.Brand_DeepGreen
import com.dmitrivenger.runo.ui.theme.Light_MutedGray
import kotlinx.coroutines.delay
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ROUTE_COLOR      = "#0F3D2E"
private const val ROUTE_GLOW_COLOR = "#A8C290"

@Composable
fun RunDetailScreen(runId: Long, app: RunoApplication, onBack: () -> Unit) {
    var run by remember { mutableStateOf<Run?>(null) }
    LaunchedEffect(runId) { run = app.runRepository.getRunById(runId) }
    val r = run ?: return

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Full-screen route map with animated pointer ───────────────────────
        if (r.routePoints.size >= 2) {
            AnimatedRouteMap(
                points = r.routePoints,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            )
        }

        // ── Back button (top-left overlay) ────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(12.dp),
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.90f)),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Brand_DeepGreen,
                )
            }
        }

        // ── Stats card (bottom sheet) ─────────────────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 12.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 28.dp, vertical = 24.dp),
            ) {
                // Date / time header
                val dayDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
                    .format(Date(r.startTime))
                val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault())
                    .format(Date(r.startTime))

                Text(
                    text = dayDate,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Brand_DeepGreen,
                )
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Light_MutedGray,
                )

                Spacer(Modifier.height(24.dp))

                // Primary 3-metric row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    StatColumn(
                        label = "Distance",
                        value = "%.2f".format(r.distanceKm),
                        unit = "km",
                    )
                    VerticalStatDivider()
                    StatColumn(
                        label = "Avg Pace",
                        value = r.formattedPace().replace(" /km", ""),
                        unit = "/km",
                    )
                    VerticalStatDivider()
                    StatColumn(
                        label = "Duration",
                        value = r.formattedDuration(),
                        unit = "",
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Calories full-width chip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brand_DeepGreen.copy(alpha = 0.08f))
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "🔥", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Calories burned",
                            style = MaterialTheme.typography.labelMedium,
                            color = Light_MutedGray,
                        )
                        Text(
                            text = "${r.caloriesBurned.toInt()} kcal",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                            ),
                            color = Brand_DeepGreen,
                        )
                    }
                }
            }
        }
    }
}

// ── Full-screen map that animates a dot along the route ───────────────────────
@Composable
private fun AnimatedRouteMap(points: List<LatLng>, modifier: Modifier = Modifier) {
    var markerSource by remember { mutableStateOf<GeoJsonSource?>(null) }

    MapLibreMapView(
        modifier = modifier,
        onMapReady = { map, style ->
            // Route glow
            style.addSource(GeoJsonSource("detail-route", buildRouteGeoJson(points)))
            style.addLayer(
                LineLayer("detail-glow", "detail-route").withProperties(
                    PropertyFactory.lineColor(ROUTE_GLOW_COLOR),
                    PropertyFactory.lineWidth(20f),
                    PropertyFactory.lineOpacity(0.35f),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                )
            )
            // Route solid line
            style.addLayer(
                LineLayer("detail-line", "detail-route").withProperties(
                    PropertyFactory.lineColor(ROUTE_COLOR),
                    PropertyFactory.lineWidth(5f),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                )
            )

            // Start dot (static, white-filled)
            style.addSource(GeoJsonSource("detail-start", buildPointGeoJson(points.first())))
            style.addLayer(
                CircleLayer("detail-start-layer", "detail-start").withProperties(
                    PropertyFactory.circleColor("#FFFFFF"),
                    PropertyFactory.circleRadius(7f),
                    PropertyFactory.circleStrokeColor(ROUTE_COLOR),
                    PropertyFactory.circleStrokeWidth(2.5f),
                )
            )

            // Moving dot
            val movingSrc = GeoJsonSource("detail-moving", buildPointGeoJson(points.first()))
            style.addSource(movingSrc)
            style.addLayer(
                CircleLayer("detail-moving-layer", "detail-moving").withProperties(
                    PropertyFactory.circleColor(ROUTE_COLOR),
                    PropertyFactory.circleRadius(10f),
                    PropertyFactory.circleStrokeColor("#FFFFFF"),
                    PropertyFactory.circleStrokeWidth(3f),
                )
            )
            markerSource = movingSrc

            // Fit camera to full route
            val boundsBuilder = LatLngBounds.Builder()
            points.forEach { boundsBuilder.include(it.toMapLibre()) }
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 80))
        }
    )

    // Animate the moving dot along the route (12-second playback, then hold at finish)
    LaunchedEffect(markerSource) {
        val src = markerSource ?: return@LaunchedEffect
        val playbackMs = 12_000L
        val stepMs = (playbackMs / points.size).coerceAtLeast(16L)
        for (point in points) {
            src.setGeoJson(buildPointGeoJson(point))
            delay(stepMs)
        }
    }
}

// ── Stat column ───────────────────────────────────────────────────────────────
@Composable
private fun StatColumn(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Light_MutedGray,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Brand_DeepGreen,
        )
        if (unit.isNotEmpty()) {
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = Light_MutedGray,
            )
        }
    }
}

@Composable
private fun VerticalStatDivider() {
    Box(
        modifier = Modifier
            .size(width = 1.dp, height = 52.dp)
            .background(Brand_DeepGreen.copy(alpha = 0.12f)),
    )
}
