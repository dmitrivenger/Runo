package com.dmitrivenger.runo.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.domain.model.LatLng
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.ui.components.MapLibreMapView
import com.dmitrivenger.runo.ui.components.RunoTopBar
import com.dmitrivenger.runo.ui.components.MetricCard
import com.dmitrivenger.runo.ui.components.PaceChart
import com.dmitrivenger.runo.ui.components.buildRouteGeoJson
import com.dmitrivenger.runo.ui.components.toMapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val BRAND_GREEN = "#22C55E"

@Composable
fun RunDetailScreen(runId: Long, app: RunoApplication, onBack: () -> Unit) {
    var run by remember { mutableStateOf<Run?>(null) }
    LaunchedEffect(runId) { run = app.runRepository.getRunById(runId) }
    val r = run ?: return

    val dayDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date(r.startTime))
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(r.startTime))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        item { RunoTopBar(onBack = onBack) }

        // ── Header ────────────────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = dayDate,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(20.dp))

                // Hero: distance large
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "%.2f".format(r.distanceKm),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "km",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }
                Spacer(Modifier.height(20.dp))
            }
        }

        // ── Stats grid ────────────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label = "Time",
                        value = r.formattedDuration(),
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label = "Pace",
                        value = r.formattedPace().replace(" /km", ""),
                        unit = "/km",
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(12.dp))
                MetricCard(
                    label = "Calories",
                    value = "${r.caloriesBurned.toInt()}",
                    unit = "kcal",
                    valueColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(28.dp))
            }
        }

        // ── Route map ─────────────────────────────────────────────────────────
        if (r.routePoints.size >= 2) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeading("Route")
                    Spacer(Modifier.height(10.dp))
                    DetailRouteMap(
                        points = r.routePoints,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .clip(RoundedCornerShape(20.dp)),
                    )
                    Spacer(Modifier.height(28.dp))
                }
            }
        }

        // ── Pace breakdown ────────────────────────────────────────────────────
        if (r.kmPaces.size >= 2) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeading("Pace breakdown")
                    Spacer(Modifier.height(10.dp))

                    val sorted = r.kmPaces.entries.sortedBy { it.key }
                    val maxPace = sorted.maxOf { it.value }.coerceAtLeast(1f)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                    ) {
                        PaceChart(
                            values = sorted.map { it.value },
                            lineColor = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    sorted.forEachIndexed { index, (km, pace) ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(vertical = 10.dp),
                            )
                        }
                        KmPaceRow(km = km, paceSeconds = pace, maxPace = maxPace)
                    }
                }
            }
        }
    }
}

@Composable
private fun KmPaceRow(km: Int, paceSeconds: Float, maxPace: Float) {
    val paceStr = "%d:%02d".format((paceSeconds / 60).toInt(), (paceSeconds % 60).toInt())
    val fillFraction = (paceSeconds / maxPace).coerceIn(0.15f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // KM badge
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$km",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(12.dp))

        // Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(fillFraction)
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }

        Spacer(Modifier.width(12.dp))
        Text(
            text = "$paceStr /km",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun DetailRouteMap(points: List<LatLng>, modifier: Modifier = Modifier) {
    MapLibreMapView(
        modifier = modifier,
        onMapReady = { map, style ->
            val source = GeoJsonSource("route", buildRouteGeoJson(points))
            style.addSource(source)
            style.addLayer(
                LineLayer("route-glow", "route").withProperties(
                    PropertyFactory.lineColor(BRAND_GREEN),
                    PropertyFactory.lineWidth(14f),
                    PropertyFactory.lineOpacity(0.25f),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                )
            )
            style.addLayer(
                LineLayer("route-line", "route").withProperties(
                    PropertyFactory.lineColor(BRAND_GREEN),
                    PropertyFactory.lineWidth(5f),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                )
            )
            val boundsBuilder = LatLngBounds.Builder()
            points.forEach { boundsBuilder.include(it.toMapLibre()) }
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 64))
        }
    )
}
